package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigAutoConfiguration
import com.jiangtj.micro.business.config.SystemConfigLoader
import com.jiangtj.micro.business.config.SystemConfigService
import com.jiangtj.micro.business.pic.upload.easyimages.EasyImagesAutoConfiguration
import com.jiangtj.micro.business.pic.upload.easyimages.EasyImagesService
import com.jiangtj.micro.business.pic.upload.local.LocalPicUploadAutoConfiguration
import com.jiangtj.micro.business.pic.upload.local.LocalPicUploadService
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * 验证各上传服务商的自动配置依赖系统配置（SystemConfig）
 */
class PicUploadSystemConfigWiringTest {

    private val runner = ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                SystemConfigAutoConfiguration::class.java,
                PicUploadAutoConfiguration::class.java,
                PicUploadDirsAutoConfiguration::class.java,
                LocalPicUploadAutoConfiguration::class.java,
                EasyImagesAutoConfiguration::class.java,
            )
        )

    @Test
    fun `providers are registered when system config enabled`() {
        runner.withPropertyValues("jmicro.system.config.enabled=true")
            .run { context ->
                assertNotNull(context.getBean(SystemConfigService::class.java))
                assertNotNull(context.getBean(LocalPicUploadService::class.java))
                assertNotNull(context.getBean(EasyImagesService::class.java))
                assertEquals(3, context.getBeansOfType(SystemConfigLoader::class.java).size)
                assertNotNull(context.getBean(PicUploadService::class.java))
            }
    }

    @Test
    fun `providers are absent when system config disabled`() {
        runner.run { context ->
            assertEquals(0, context.getBeansOfType(SystemConfigLoader::class.java).size)
            assertEquals(0, context.getBeansOfType(PicUploadProvider::class.java).size)
            assertNotNull(context.getBean(PicUploadService::class.java))
        }
    }
}
