package com.jiangtj.micro.business.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.*
import java.util.function.Function

class SystemConfigServiceTest {

    private lateinit var saver: SystemConfigSaver
    private lateinit var publisher: org.springframework.context.ApplicationEventPublisher
    private lateinit var properties: SystemConfigProperties
    private lateinit var loader: SystemConfigLoader
    private lateinit var service: SystemConfigService

    @BeforeEach
    fun setup() {
        saver = mock()
        publisher = mock()
        properties = SystemConfigProperties()
        loader = mock()
    }

    private fun defaultItem(
        key: String,
        value: String = "default",
        secret: Boolean = false,
        formatter: Function<String, String>? = null,
        valueFormatter: Function<String, String>? = null,
        group: SystemGroup = SystemGroup("default"),
        order: Int = 999,
        tag: List<String> = listOf(),
    ): SystemItemInfo = SystemItemInfo(
        key = key,
        name = key,
        value = value,
        secret = secret,
        formatter = formatter,
        valueFormatter = valueFormatter,
        group = group,
        order = order,
        tag = tag,
    )

    private fun buildService(vararg items: SystemItemInfo): SystemConfigService {
        whenever(loader.load()).thenReturn(items.toList())
        return SystemConfigService(
            listOf(loader),
            publisher,
            properties,
            saver,
        )
    }

    enum class FakeKey { Theme }

    // ---- 初始化与 trimKey 归一化 ----

    @Test
    fun `init normalizes loader keys via trimKey`() {
        val item = defaultItem(key = "my-Key_1 .x")
        service = buildService(item)

        // trimKey 去掉 - _ 空格 . 并转小写 => "mykey1x"
        assertEquals("default", service.getValue("my-Key_1 .x"))
        assertEquals("default", service.getValue("MYKEY1X"))
    }

    @Test
    fun `init applies properties kv when enabled`() {
        properties.enabled = true
        properties.kv = mutableMapOf("my-key" to "fromProps")
        val item = defaultItem(key = "my-key", value = "default")
        service = buildService(item)

        assertEquals("fromProps", service.getValue("my-key"))
    }

    @Test
    fun `init ignores properties kv when disabled`() {
        properties.enabled = false
        properties.kv = mutableMapOf("my-key" to "fromProps")
        val item = defaultItem(key = "my-key", value = "default")
        service = buildService(item)

        assertEquals("default", service.getValue("my-key"))
    }

    @Test
    fun `init ignores unknown kv keys`() {
        properties.enabled = true
        properties.kv = mutableMapOf("not-exist" to "x")
        val item = defaultItem(key = "my-key", value = "default")
        service = buildService(item)

        assertEquals("default", service.getValue("my-key"))
    }

    // ---- getValue ----

    @Test
    fun `getValue returns saver value when present`() {
        val item = defaultItem(key = "k")
        service = buildService(item)
        whenever(saver.fetchOneByConfigKey("k")).thenReturn("saved")

        assertEquals("saved", service.getValue("k"))
        // 缓存生效：再次获取不重复查 saver
        assertEquals("saved", service.getValue("k"))
    }

    @Test
    fun `getValue falls back to default when saver returns null`() {
        val item = defaultItem(key = "k", value = "default")
        service = buildService(item)
        whenever(saver.fetchOneByConfigKey("k")).thenReturn(null)

        assertEquals("default", service.getValue("k"))
    }

    @Test
    fun `getValue returns empty when neither saver nor default`() {
        service = buildService()
        whenever(saver.fetchOneByConfigKey("unknown")).thenReturn(null)

        assertEquals("", service.getValue("unknown"))
    }

    @Test
    fun `getValue by enum delegates to name`() {
        val item = defaultItem(key = "Theme", value = "dark")
        service = buildService(item)
        whenever(saver.fetchOneByConfigKey("Theme")).thenReturn(null)

        assertEquals("dark", service.getValue(FakeKey.Theme))
    }

    // ---- isTrue ----

    @Test
    fun `isTrue handles true value`() {
        val item = defaultItem(key = "flag", value = "true")
        service = buildService(item)
        whenever(saver.fetchOneByConfigKey("flag")).thenReturn(null)

        assertTrue(service.isTrue("flag"))
    }

    @Test
    fun `isTrue handles false value`() {
        val item = defaultItem(key = "flag", value = "false")
        service = buildService(item)
        whenever(saver.fetchOneByConfigKey("flag")).thenReturn(null)

        assertFalse(service.isTrue("flag"))
        assertEquals("false", service.getValue("flag"))
    }

    @Test
    fun `isTrue handles case insensitive`() {
        val item = defaultItem(key = "flag", value = "TRUE")
        service = buildService(item)
        whenever(saver.fetchOneByConfigKey("flag")).thenReturn(null)

        assertTrue(service.isTrue("flag"))
    }

    // ---- getAllConfig ----

    @Test
    fun `getAllConfig returns defaults sorted by group and order`() {
        val a = defaultItem(key = "a", value = "1", group = SystemGroup("g", 2), order = 1)
        val b = defaultItem(key = "b", value = "2", group = SystemGroup("g", 1), order = 5)
        val c = defaultItem(key = "c", value = "3", group = SystemGroup("g", 1), order = 1)
        service = buildService(a, b, c)
        whenever(saver.findAll()).thenReturn(emptyList())

        val result = service.getAllConfig().map { it.key }
        // group order: g(1) before g(2); within g(1): c(1) before b(5)
        assertEquals(listOf("c", "b", "a"), result)
    }

    @Test
    fun `getAllConfig marks modified from saver`() {
        val item = defaultItem(key = "k", value = "default")
        service = buildService(item)
        whenever(saver.findAll()).thenReturn(listOf("k" to "saved"))

        val result = service.getAllConfig()
        assertEquals(1, result.size)
        assertEquals("saved", result[0].value)
        assertTrue(result[0].isModified)
    }

    @Test
    fun `getAllConfig masks secret values`() {
        val item = defaultItem(key = "pwd", value = "s3cret", secret = true)
        service = buildService(item)
        whenever(saver.findAll()).thenReturn(emptyList())

        val result = service.getAllConfig()
        assertEquals("******", result[0].value)
    }

    @Test
    fun `getAllConfig applies formatter`() {
        val item = defaultItem(
            key = "k",
            value = "raw",
            formatter = Function { "fmt:$it" },
        )
        service = buildService(item)
        whenever(saver.findAll()).thenReturn(emptyList())

        val result = service.getAllConfig()
        assertEquals("fmt:raw", result[0].formatedValue)
    }

    @Test
    fun `getAllConfig ignores saver keys not in defaults`() {
        val item = defaultItem(key = "k", value = "default")
        service = buildService(item)
        whenever(saver.findAll()).thenReturn(listOf("unknown" to "x"))

        val result = service.getAllConfig()
        assertEquals(1, result.size)
        assertEquals("k", result[0].key)
    }

    // ---- generateConfig ----

    @Test
    fun `generateConfig with blank value`() {
        val item = defaultItem(key = "My_Key", value = "default")
        service = buildService(item)

        assertEquals("system.config.kv.my-key=", service.generateConfig("My_Key", null))
        assertEquals("system.config.kv.my-key=", service.generateConfig("My_Key", ""))
        assertEquals("system.config.kv.my-key=", service.generateConfig("My_Key", "   "))
    }

    @Test
    fun `generateConfig with value applies valueFormatter`() {
        val item = defaultItem(
            key = "k",
            value = "default",
            valueFormatter = Function { "v:$it" },
        )
        service = buildService(item)

        assertEquals("system.config.kv.k=v:input", service.generateConfig("k", "input"))
    }

    @Test
    fun `generateConfig normalizes key`() {
        val item = defaultItem(key = "My_Key", value = "default")
        service = buildService(item)

        assertEquals("system.config.kv.my-key=default", service.generateConfig("My_Key", "default"))
    }

    // ---- updateConfig ----

    @Test
    fun `updateConfig saves publishes and caches`() {
        val item = defaultItem(key = "k", value = "default")
        service = buildService(item)

        service.updateConfig("k", "new")

        verify(saver).save(eq("k"), eq("new"))
        verify(publisher).publishEvent(any<SystemConfigUpdateEvent>())
        assertEquals("new", service.getValue("k"))
    }

    @Test
    fun `updateConfig applies valueFormatter`() {
        val item = defaultItem(
            key = "k",
            value = "default",
            valueFormatter = Function { "v:$it" },
        )
        service = buildService(item)

        service.updateConfig("k", "input")

        verify(saver).save(eq("k"), eq("v:input"))
        assertEquals("v:input", service.getValue("k"))
    }

    @Test
    fun `updateConfig throws when item not exists`() {
        service = buildService()

        val ex = assertThrows(MicroConfigException::class.java) {
            service.updateConfig("missing", "x")
        }
        assertTrue(ex.message!!.contains("配置项不存在"))
    }

    @Test
    fun `updateConfig throws when valueFormatter fails`() {
        val item = defaultItem(
            key = "k",
            value = "default",
            valueFormatter = Function { throw IllegalArgumentException("bad") },
        )
        service = buildService(item)

        val ex = assertThrows(MicroConfigException::class.java) {
            service.updateConfig("k", "x")
        }
        assertTrue(ex.message!!.contains("配置项值不合法"))
    }

    // ---- deleteConfig ----

    @Test
    fun `deleteConfig removes and reverts to default`() {
        val item = defaultItem(key = "k", value = "default")
        service = buildService(item)
        whenever(saver.fetchOneByConfigKey("k")).thenReturn(null)

        // 先更新到缓存
        service.updateConfig("k", "new")
        assertEquals("new", service.getValue("k"))

        service.deleteConfig("k")
        verify(saver).delete(eq("k"))
        verify(publisher, times(2)).publishEvent(any<SystemConfigUpdateEvent>())
        assertEquals("default", service.getValue("k"))
    }

    @Test
    fun `deleteConfig publishes event with reverted value`() {
        val item = defaultItem(key = "k", value = "default")
        service = buildService(item)

        service.updateConfig("k", "new")
        service.deleteConfig("k")

        // 事件的新值应为默认值
        val captor = ArgumentCaptor.forClass(Any::class.java)
        verify(publisher, times(2)).publishEvent(captor.capture())
        val updateEvents = captor.allValues.filterIsInstance<SystemConfigUpdateEvent>()
        assertEquals(2, updateEvents.size)
        assertEquals("k", updateEvents[1].key)
        assertEquals("default", updateEvents[1].newV)
    }

    // ---- refreshConfig ----

    @Test
    fun `refreshConfig invalidates cache and publishes event`() {
        val item = defaultItem(key = "k", value = "default")
        service = buildService(item)
        whenever(saver.fetchOneByConfigKey("k")).thenReturn("saved")

        assertEquals("saved", service.getValue("k"))
        // 修改底层 saver 返回（但缓存未失效时应仍返回旧值）
        whenever(saver.fetchOneByConfigKey("k")).thenReturn("saved2")
        assertEquals("saved", service.getValue("k"))

        service.refreshConfig()
        verify(publisher).publishEvent(any<SystemConfigRefreshEvent>())
        assertEquals("saved2", service.getValue("k"))
    }

    // ---- getConfigByTag ----

    @Test
    fun `getConfigByTag filters case insensitively`() {
        val a = defaultItem(key = "a", tag = listOf("Public", "x"))
        val b = defaultItem(key = "b", tag = listOf("Y"))
        service = buildService(a, b)
        whenever(saver.findAll()).thenReturn(emptyList())

        val result = service.getConfigByTag("public").map { it.key }
        assertEquals(listOf("a"), result)
    }

    @Test
    fun `getConfigByTag trims and lowercases input`() {
        val a = defaultItem(key = "a", tag = listOf("pub"))
        service = buildService(a)
        whenever(saver.findAll()).thenReturn(emptyList())

        val result = service.getConfigByTag("  PUB  ").map { it.key }
        assertEquals(listOf("a"), result)
    }

    @Test
    fun `getConfigByTag returns empty when no match`() {
        val a = defaultItem(key = "a", tag = listOf("x"))
        service = buildService(a)
        whenever(saver.findAll()).thenReturn(emptyList())

        assertTrue(service.getConfigByTag("none").isEmpty())
    }
}
