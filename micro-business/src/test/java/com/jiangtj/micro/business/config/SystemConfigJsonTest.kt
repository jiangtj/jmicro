package com.jiangtj.micro.business.config

import com.jiangtj.micro.common.JsonUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.lang.reflect.Type

class SystemConfigJsonTest {

    data class Item(
        val path: String,
        val size: Long? = null,
    )

    private lateinit var saver: SystemConfigSaver
    private lateinit var publisher: org.springframework.context.ApplicationEventPublisher
    private lateinit var properties: SystemConfigProperties

    private val itemMapType: Type = JsonUtils.jsonMapper.typeFactory
        .constructMapType(LinkedHashMap::class.java, String::class.java, Item::class.java)

    private val itemListType: Type = JsonUtils.jsonMapper.typeFactory
        .constructCollectionType(ArrayList::class.java, Item::class.java)

    private val mapKey = "jmicro.test.map"
    private val listKey = "jmicro.test.list"

    private val mapItem = SystemItemInfo(
        key = mapKey,
        name = "map",
        value = """{"a":{"path":"/a","size":1}}""",
        type = SystemItemType.JSON.name,
        jsonType = itemMapType,
    )

    private val listItem = SystemItemInfo(
        key = listKey,
        name = "list",
        value = """[{"path":"/a"},{"path":"/b"}]""",
        type = SystemItemType.JSON.name,
        jsonType = itemListType,
    )

    @BeforeEach
    fun setup() {
        saver = mock()
        publisher = mock()
        properties = SystemConfigProperties()
        whenever(saver.fetchOneByConfigKey(any())).thenReturn(null)
        whenever(saver.findAll()).thenReturn(emptyList())
    }

    private fun buildService(vararg items: SystemItemInfo): SystemConfigService {
        val loader = object : SystemConfigLoader {
            override fun load(): List<SystemItemInfo> = items.toList()
        }
        return SystemConfigService(listOf(loader), publisher, properties, saver)
    }

    // ---- 读取 ----

    @Test
    fun `getMap parses json value`() {
        val service = buildService(mapItem)
        val map = service.getMap(mapKey, Item::class.java)

        assertEquals(setOf("a"), map.keys)
        assertEquals("/a", map["a"]!!.path)
        assertEquals(1L, map["a"]!!.size)
    }

    @Test
    fun `getMap returns empty when value blank`() {
        val service = buildService(mapItem.copy(value = ""))
        assertTrue(service.getMap(mapKey, Item::class.java).isEmpty())
    }

    @Test
    fun `getList parses json value`() {
        val service = buildService(listItem)
        assertEquals(listOf("/a", "/b"), service.getList(listKey, Item::class.java).map { it.path })
    }

    @Test
    fun `getObject parses json value`() {
        val single = SystemItemInfo(
            key = "single", name = "single", value = """{"path":"/x"}""",
            type = SystemItemType.JSON.name, jsonType = Item::class.java,
        )
        val service = buildService(single)
        assertEquals("/x", service.getObject("single", Item::class.java)!!.path)
    }

    @Test
    fun `getConfigField supports dot and pointer path`() {
        val service = buildService(mapItem)
        assertEquals("/a", service.getConfigField(mapKey, "a.path"))
        assertEquals("/a", service.getConfigField(mapKey, "/a/path"))
        assertNull(service.getConfigField(mapKey, "b.path"))
    }

    @Test
    fun `getMapEntry returns entry`() {
        val service = buildService(mapItem)
        @Suppress("UNCHECKED_CAST")
        val entry = service.getMapEntry(mapKey, "a") as Map<String, Any?>
        assertEquals("/a", entry["path"])
    }

    // ---- 写入校验与规范化 ----

    @Test
    fun `updateConfig rejects invalid json`() {
        val service = buildService(mapItem)
        val ex = assertThrows(MicroConfigException::class.java) {
            service.updateConfig(mapKey, "{not-json")
        }
        assertTrue(ex.message!!.contains("配置项值不合法"))
    }

    @Test
    fun `updateConfig rejects json not matching jsonType`() {
        val service = buildService(mapItem)
        val ex = assertThrows(MicroConfigException::class.java) {
            service.updateConfig(mapKey, """{"a":{"size":1}}""")
        }
        assertTrue(ex.message!!.contains("配置项值不合法"))
    }

    @Test
    fun `updateConfig normalizes json to compact single line`() {
        val service = buildService(mapItem)
        service.updateConfig(mapKey, """
            {
              "a": {"path": "/a"}
            }
        """.trimIndent())

        val captor = argumentCaptor<String>()
        verify(saver).save(eq(mapKey), captor.capture())
        assertFalse(captor.firstValue.contains("\n"))
        assertEquals("""{"a":{"path":"/a"}}""", captor.firstValue)
    }

    @Test
    fun `updateConfig allows blank json value`() {
        val service = buildService(mapItem)
        service.updateConfig(mapKey, "  ")
        verify(saver).save(eq(mapKey), eq(""))
    }

    @Test
    fun `generateConfig outputs single line json`() {
        val service = buildService(mapItem)
        val line = service.generateConfig(mapKey, """{"a":{"path":"/a"}}""")
        assertEquals("jmicro.system.config.kv.jmicro.test.map={\"a\":{\"path\":\"/a\"}}", line)
        assertFalse(line.contains("\n"))
    }

    // ---- Map 操作 ----

    @Test
    fun `updateMapEntry adds and updates entry`() {
        val service = buildService(mapItem)
        service.updateMapEntry(mapKey, "b", mapOf("path" to "/b"))

        val map = service.getMap(mapKey, Item::class.java)
        assertEquals(setOf("a", "b"), map.keys)
        assertEquals("/b", map["b"]!!.path)
        assertEquals("/a", map["a"]!!.path)
    }

    @Test
    fun `removeMapEntry removes entry`() {
        val service = buildService(mapItem)
        service.removeMapEntry(mapKey, "a")

        assertTrue(service.getMap(mapKey, Item::class.java).isEmpty())
    }

    @Test
    fun `removeMapEntry ignores missing entry`() {
        val service = buildService(mapItem)
        service.removeMapEntry(mapKey, "not-exists")

        verify(saver, never()).save(any(), any())
    }

    @Test
    fun `updateConfigField creates nested path`() {
        val service = buildService(mapItem)
        service.updateConfigField(mapKey, "a.meta", mapOf("x" to 1))

        @Suppress("UNCHECKED_CAST")
        val meta = service.getConfigField(mapKey, "a.meta") as Map<String, Any?>
        assertEquals(1, meta["x"])
    }

    // ---- List 操作 ----

    @Test
    fun `addListElement appends by default`() {
        val service = buildService(listItem)
        service.addListElement(listKey, mapOf("path" to "/c"))

        assertEquals(listOf("/a", "/b", "/c"), service.getList(listKey, Item::class.java).map { it.path })
    }

    @Test
    fun `addListElement inserts at index`() {
        val service = buildService(listItem)
        service.addListElement(listKey, mapOf("path" to "/c"), 1)

        assertEquals(listOf("/a", "/c", "/b"), service.getList(listKey, Item::class.java).map { it.path })
    }

    @Test
    fun `updateListElement replaces element`() {
        val service = buildService(listItem)
        service.updateListElement(listKey, 0, mapOf("path" to "/z"))

        assertEquals(listOf("/z", "/b"), service.getList(listKey, Item::class.java).map { it.path })
    }

    @Test
    fun `updateListElement rejects index out of range`() {
        val service = buildService(listItem)
        assertThrows(MicroConfigException::class.java) {
            service.updateListElement(listKey, 5, mapOf("path" to "/z"))
        }
    }

    @Test
    fun `removeListElement removes element`() {
        val service = buildService(listItem)
        service.removeListElement(listKey, 1)

        assertEquals(listOf("/a"), service.getList(listKey, Item::class.java).map { it.path })
    }

    // ---- 脱敏 ----

    @Test
    fun `getAllConfig masks secretPaths`() {
        val secretItem = mapItem.copy(secretPaths = listOf("a.path"))
        val service = buildService(secretItem)

        val config = service.getAllConfig().single()
        assertEquals("""{"a":{"path":"******","size":1}}""", config.value)
    }

    @Test
    fun `getAllConfig does not add masked field when path missing`() {
        val secretItem = mapItem.copy(secretPaths = listOf("a.notExists"))
        val service = buildService(secretItem)

        val config = service.getAllConfig().single()
        assertEquals("""{"a":{"path":"/a","size":1}}""", config.value)
    }

    @Test
    fun `updateConfig restores masked field from old value`() {
        val secretItem = mapItem.copy(secretPaths = listOf("a.path"))
        val service = buildService(secretItem)
        whenever(saver.fetchOneByConfigKey(mapKey)).thenReturn("""{"a":{"path":"/real","size":1}}""")

        service.updateConfig(mapKey, """{"a":{"path":"******","size":2}}""")

        val captor = argumentCaptor<String>()
        verify(saver).save(eq(mapKey), captor.capture())
        assertEquals("""{"a":{"path":"/real","size":2}}""", captor.firstValue)
    }

    @Test
    fun `secret whole item still wins when secretPaths empty`() {
        val secretItem = mapItem.copy(secret = true)
        val service = buildService(secretItem)

        assertEquals("******", service.getAllConfig().single().value)
    }

    // ---- schema ----

    @Test
    fun `getAllConfig generates schema for json item`() {
        val service = buildService(mapItem)
        val schema = service.getAllConfig().single().schema

        assertNotNull(schema)
        assertEquals("object", schema!!.type)
        assertNotNull(schema.additionalProperties)
        assertNotNull(schema.additionalProperties!!.properties!!["path"])
    }

    @Test
    fun `getAllConfig generates array schema for list item`() {
        val service = buildService(listItem)
        val schema = service.getAllConfig().single().schema

        assertNotNull(schema)
        assertEquals("array", schema!!.type)
        assertNotNull(schema.items)
    }

    @Test
    fun `non json item has no schema`() {
        val text = SystemItemInfo(key = "text", name = "text", value = "v")
        val service = buildService(text)

        assertNull(service.getAllConfig().single().schema)
    }
}
