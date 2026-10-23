package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigLoader
import com.jiangtj.micro.business.config.SystemConfigProperties
import com.jiangtj.micro.business.config.SystemConfigSaver
import com.jiangtj.micro.business.config.SystemConfigService
import com.jiangtj.micro.business.config.SystemItemInfo
import com.jiangtj.micro.business.pic.upload.ex.PicUploadCheckException
import com.jiangtj.micro.business.pic.upload.local.LocalPicUploadConfigItem
import com.jiangtj.micro.business.pic.upload.local.LocalPicUploadService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.mock
import org.springframework.context.ApplicationEventPublisher
import org.springframework.mock.web.MockMultipartFile
import org.springframework.util.unit.DataSize
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LocalPicUploadServiceTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var dir: PicUploadProperties.Dir
    private lateinit var picUploadService: PicUploadService

    @BeforeEach
    fun setUp() {
        val systemConfigService = SystemConfigService(
            listOf(
                object : SystemConfigLoader {
                    override fun load(): List<SystemItemInfo> = listOf(
                        SystemItemInfo(
                            key = LocalPicUploadConfigItem.UPLOAD_PATH.key,
                            name = "上传路径",
                            value = tempDir.toString(),
                        ),
                        SystemItemInfo(
                            key = LocalPicUploadConfigItem.URL.key,
                            name = "访问地址",
                            value = "http://localhost:8080",
                        ),
                    )
                },
            ),
            mock<ApplicationEventPublisher>(),
            SystemConfigProperties(),
            mock<SystemConfigSaver>(),
        )

        val properties = PicUploadProperties()
        val dirs = HashMap<String, PicUploadProperties.Dir>()
        dir = PicUploadProperties.Dir()
        dir.path = "test"
        dirs["test"] = dir
        properties.dirs = dirs

        picUploadService = PicUploadService(properties, listOf(LocalPicUploadService(systemConfigService)))
    }

    @Test
    fun `test upload valid image`() {
        // 创建模拟的图片文件
        val file = MockMultipartFile("test-image", "test-image.jpg", "image/jpeg", ByteArray(1024))

        val result = picUploadService.upload("test", file)

        assertNotNull(result)
        assertEquals("test-image.jpg", result.originalFileName)
        assertTrue(result.fileName.endsWith(".jpg"))
        assertTrue(result.fileUrl.startsWith("http://localhost:8080/"))
    }

    @Test
    fun `test upload invalid extension`() {
        val file = MockMultipartFile("test-file", "test-file.exe", "application/octet-stream", ByteArray(1024))

        val exception = assertThrows<PicUploadCheckException> {
            picUploadService.upload("test", file)
        }
        assertTrue(exception.message!!.contains("不支持的文件类型"))
    }

    @Test
    fun `test upload exceed max size`() {
        // 设置最大文件大小为 1kb
        dir.maxFileSize = DataSize.ofKilobytes(1)

        val file = MockMultipartFile("test-image", "test-image.jpg", "image/jpeg", ByteArray(2000))

        val exception = assertThrows<PicUploadCheckException> {
            picUploadService.upload("test", file)
        }
        assertTrue(exception.message!!.contains("文件大小超过限制"))
    }

    @Test
    fun `test data size`() {
        assertEquals(1024 * 1024, DataSize.ofMegabytes(1).toBytes())
        assertEquals(1024, DataSize.ofKilobytes(1).toBytes())
    }
}
