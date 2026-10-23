package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigLoader
import com.jiangtj.micro.business.config.SystemConfigProperties
import com.jiangtj.micro.business.config.SystemConfigSaver
import com.jiangtj.micro.business.config.SystemConfigService
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType
import com.jiangtj.micro.common.JsonUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.context.ApplicationEventPublisher
import org.springframework.util.unit.DataSize

class PicUploadDirsConfigTest {

    private fun dirsItem(value: String) = SystemItemInfo(
        key = PicUploadConfigLoader.KEY,
        name = "dirs",
        value = value,
        type = SystemItemType.JSON.name,
        jsonType = JsonUtils.jsonMapper.typeFactory
            .constructMapType(LinkedHashMap::class.java, String::class.java, PicUploadProperties.Dir::class.java),
    )

    private fun buildService(vararg items: SystemItemInfo): SystemConfigService {
        val loader = object : SystemConfigLoader {
            override fun load(): List<SystemItemInfo> = items.toList()
        }
        return SystemConfigService(
            listOf(loader),
            mock<ApplicationEventPublisher>(),
            SystemConfigProperties(),
            mock<SystemConfigSaver>(),
        )
    }

    @Test
    fun `loader exposes dirs as json item`() {
        val properties = PicUploadProperties()
        val dir = PicUploadProperties.Dir()
        dir.path = "/avatar"
        properties.dirs = hashMapOf("avatar" to dir)

        val item = PicUploadConfigLoader(properties).load().single { it.key == PicUploadConfigLoader.KEY }
        assertEquals(PicUploadConfigLoader.KEY, item.key)
        assertEquals(SystemItemType.JSON.name, item.type)
        assertNotNull(item.jsonType)
        assertTrue(item.value.contains("\"path\":\"/avatar\""))
    }

    @Test
    fun `service reads dirs from system config`() {
        val properties = PicUploadProperties()
        val service = buildService(
            dirsItem("""{"avatar":{"path":"/avatar","maxFileSize":2048,"allowedExtensions":["png"]}}""")
        )
        val picUploadService = PicUploadService(properties, emptyList(), service)

        val dir = picUploadService.getDir("avatar")
        assertEquals("/avatar", dir.path)
        assertEquals(2048, dir.maxFileSize!!.toBytes())
        assertArrayEquals(arrayOf("png"), dir.allowedExtensions)
        // 未配置时回退到 properties 的默认服务商
        assertEquals("local", dir.provider)
    }

    @Test
    fun `service falls back to properties when system config empty`() {
        val properties = PicUploadProperties()
        val dir = PicUploadProperties.Dir()
        dir.path = "/from-props"
        properties.dirs = hashMapOf("avatar" to dir)

        val service = buildService(dirsItem(""))
        val picUploadService = PicUploadService(properties, emptyList(), service)

        assertEquals("/from-props", picUploadService.getDir("avatar").path)
    }

    @Test
    fun `service falls back to properties when system config disabled`() {
        val properties = PicUploadProperties()
        val dir = PicUploadProperties.Dir()
        dir.path = "/from-props"
        properties.dirs = hashMapOf("avatar" to dir)

        val picUploadService = PicUploadService(properties, emptyList())
        assertEquals("/from-props", picUploadService.getDir("avatar").path)
    }

    @Test
    fun `dirs update takes effect immediately`() {
        val properties = PicUploadProperties()
        val service = buildService(dirsItem("""{"avatar":{"path":"/old"}}"""))
        val picUploadService = PicUploadService(properties, emptyList(), service)
        assertEquals("/old", picUploadService.getDir("avatar").path)

        service.updateMapEntry(PicUploadConfigLoader.KEY, "avatar", mapOf("path" to "/new"))
        assertEquals("/new", picUploadService.getDir("avatar").path)
    }

    @Test
    fun `dir json serializes datasize as bytes`() {
        val dir = PicUploadProperties.Dir()
        dir.path = "/a"
        dir.maxFileSize = DataSize.ofKilobytes(2)

        val json = JsonUtils.toJson(dir)
        assertNotNull(json)
        assertTrue(json!!.contains("\"maxFileSize\":2048"))
        // 未设置的字段不进入 JSON
        assertFalse(json.contains("provider"))
    }

    @Test
    fun `loader exposes base config items from bound properties`() {
        val properties = PicUploadProperties()
        properties.provider = "minio"
        properties.maxFileSize = DataSize.ofMegabytes(5)

        val items = PicUploadConfigLoader(properties).load().associateBy { it.key }
        val providerItem = items.getValue(PicUploadBaseConfigItem.PROVIDER.key)
        assertEquals(SystemItemType.TEXT.name, providerItem.type)
        assertEquals("minio", providerItem.value)
        assertEquals(
            "jpg,jpeg,png,gif,webp,bmp,svg,avif",
            items.getValue(PicUploadBaseConfigItem.ALLOWED_EXTENSIONS.key).value,
        )
        assertEquals(
            5,
            DataSize.parse(items.getValue(PicUploadBaseConfigItem.MAX_FILE_SIZE.key).value).toMegabytes(),
        )
    }

    @Test
    fun `service reads base defaults from system config`() {
        val properties = PicUploadProperties()
        val service = buildService(
            SystemItemInfo(key = PicUploadBaseConfigItem.PROVIDER.key, value = "minio"),
            SystemItemInfo(key = PicUploadBaseConfigItem.ALLOWED_EXTENSIONS.key, value = "png, webp"),
            SystemItemInfo(key = PicUploadBaseConfigItem.MAX_FILE_SIZE.key, value = "2KB"),
            dirsItem("""{"avatar":{"path":"/avatar"}}"""),
        )
        val picUploadService = PicUploadService(properties, emptyList(), service)

        val dir = picUploadService.getDir("avatar")
        assertEquals("minio", dir.provider)
        assertEquals(2048, dir.maxFileSize!!.toBytes())
        assertArrayEquals(arrayOf("png", "webp"), dir.allowedExtensions)
    }
}
