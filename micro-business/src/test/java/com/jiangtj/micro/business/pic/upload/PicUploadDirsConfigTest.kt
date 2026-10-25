package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.JMicroConfigLoader
import com.jiangtj.micro.business.config.JMicroConfigProperties
import com.jiangtj.micro.business.config.JMicroConfigSaver
import com.jiangtj.micro.business.config.JMicroConfigService
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.config.SystemItemType
import com.jiangtj.micro.business.config.jackson.formatDataSize
import com.jiangtj.micro.business.pic.upload.ex.PicUploadCheckException
import com.jiangtj.micro.common.JsonUtils
import com.jiangtj.micro.common.schema.JsonSchemaGenerator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    private fun buildService(vararg items: SystemItemInfo): JMicroConfigService {
        val loader = object : JMicroConfigLoader {
            override fun load(): List<SystemItemInfo> = items.toList()
        }
        return JMicroConfigService(
            listOf(loader),
            mock<ApplicationEventPublisher>(),
            JMicroConfigProperties(),
            mock<JMicroConfigSaver>(),
        )
    }

    @Test
    fun `loader exposes dirs as json item`() {
        val item = PicUploadConfigLoader().load().single { it.key == PicUploadConfigLoader.KEY }
        assertEquals(PicUploadConfigLoader.KEY, item.key)
        assertEquals(SystemItemType.JSON.name, item.type)
        assertNotNull(item.jsonType)
        assertEquals("{}", item.value)
    }

    @Test
    fun `loader exposes base config items with defaults`() {
        val items = PicUploadConfigLoader().load().associateBy { it.key }

        val providerItem = items.getValue(PicUploadBaseConfigItem.PROVIDER.key)
        assertEquals(SystemItemType.TEXT.name, providerItem.type)
        assertEquals("local", providerItem.value)
        assertEquals(
            "jpg,jpeg,png,gif,webp,bmp,svg,avif",
            items.getValue(PicUploadBaseConfigItem.ALLOWED_EXTENSIONS.key).value,
        )
        assertEquals(
            10,
            DataSize.parse(items.getValue(PicUploadBaseConfigItem.MAX_FILE_SIZE.key).value).toMegabytes(),
        )
    }

    @Test
    fun `service reads dirs from system config`() {
        val service = buildService(
            dirsItem("""{"avatar":{"path":"/avatar","maxFileSize":2048,"allowedExtensions":["png"]}}""")
        )
        val picUploadService = PicUploadService(emptyList(), service)

        val dir = picUploadService.getDir("avatar")
        assertEquals("/avatar", dir.path)
        assertEquals(2048, dir.maxFileSize!!.toBytes())
        assertArrayEquals(arrayOf("png"), dir.allowedExtensions)
        // 未配置时回退到配置项默认的服务商
        assertEquals("local", dir.provider)
    }

    @Test
    fun `service requires dirs from system config`() {
        val picUploadService = PicUploadService(emptyList(), null)

        val exception = assertThrows<PicUploadCheckException> {
            picUploadService.getDir("avatar")
        }
        assertTrue(exception.message!!.contains("不存在上传的目标定义"))
    }

    @Test
    fun `dirs update takes effect immediately`() {
        val service = buildService(dirsItem("""{"avatar":{"path":"/old"}}"""))
        val picUploadService = PicUploadService(emptyList(), service)
        assertEquals("/old", picUploadService.getDir("avatar").path)

        service.updateMapEntry(PicUploadConfigLoader.KEY, "avatar", mapOf("path" to "/new"))
        assertEquals("/new", picUploadService.getDir("avatar").path)
    }

    @Test
    fun `dir json serializes datasize as readable string`() {
        val dir = PicUploadProperties.Dir()
        dir.path = "/a"
        dir.maxFileSize = DataSize.ofKilobytes(2)

        val json = JsonUtils.toJson(dir)
        assertNotNull(json)
        assertTrue(json!!.contains("\"maxFileSize\":\"2KB\""))
        // 未设置的字段不进入 JSON
        assertFalse(json.contains("provider"))

        // 序列化结果可被反序列化回原值
        val parsed = JsonUtils.fromJson(json, PicUploadProperties.Dir::class.java)!!
        assertEquals(2048, parsed.maxFileSize!!.toBytes())
    }

    @Test
    fun `datasize string round trip`() {
        assertEquals("10MB", formatDataSize(DataSize.ofMegabytes(10)))
        assertEquals("1TB", formatDataSize(DataSize.ofTerabytes(1)))
        assertEquals("1536B", formatDataSize(DataSize.ofBytes(1536)))
        assertEquals("0B", formatDataSize(DataSize.ofBytes(0)))
    }

    @Test
    fun `dirs schema describes datasize as string`() {
        val item = PicUploadConfigLoader().load().single { it.key == PicUploadConfigLoader.KEY }
        val schema = JsonSchemaGenerator.generate(item.jsonType!!)
        val dir = schema.additionalProperties!!
        val maxFileSize = dir.properties!!["maxFileSize"]!!
        assertEquals("string", maxFileSize.type)
    }

    @Test
    fun `service reads base defaults from system config`() {
        val service = buildService(
            SystemItemInfo(key = PicUploadBaseConfigItem.PROVIDER.key, value = "minio"),
            SystemItemInfo(key = PicUploadBaseConfigItem.ALLOWED_EXTENSIONS.key, value = "png, webp"),
            SystemItemInfo(key = PicUploadBaseConfigItem.MAX_FILE_SIZE.key, value = "2KB"),
            dirsItem("""{"avatar":{"path":"/avatar"}}"""),
        )
        val picUploadService = PicUploadService(emptyList(), service)

        val dir = picUploadService.getDir("avatar")
        assertEquals("minio", dir.provider)
        assertEquals(2048, dir.maxFileSize!!.toBytes())
        assertArrayEquals(arrayOf("png", "webp"), dir.allowedExtensions)
    }
}
