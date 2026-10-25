package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.JMicroConfigAutoConfiguration
import com.jiangtj.micro.business.config.JMicroConfigLoader
import com.jiangtj.micro.business.config.JMicroConfigService
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
 * 验证各上传服务商的自动配置依赖系统配置（JMicroConfig）
 */
class PicUploadJMicroConfigWiringTest {

    private val runner = ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                JMicroConfigAutoConfiguration::class.java,
                PicUploadAutoConfiguration::class.java,
                LocalPicUploadAutoConfiguration::class.java,
                EasyImagesAutoConfiguration::class.java,
            )
        )

    @Test
    fun `providers are registered when system config enabled`() {
        runner.withPropertyValues("jmicro.config.enabled=true")
            .run { context ->
                assertNotNull(context.getBean(JMicroConfigService::class.java))
                assertNotNull(context.getBean(PicUploadConfigLoader::class.java))
                assertNotNull(context.getBean(LocalPicUploadService::class.java))
                assertNotNull(context.getBean(EasyImagesService::class.java))
                assertEquals(3, context.getBeansOfType(JMicroConfigLoader::class.java).size)
                assertNotNull(context.getBean(PicUploadService::class.java))
            }
    }

    @Test
    fun `config loader is loaded at startup while providers are absent when system config disabled`() {
        runner.run { context ->
            assertEquals(1, context.getBeansOfType(JMicroConfigLoader::class.java).size)
            assertEquals(0, context.getBeansOfType(PicUploadProvider::class.java).size)
            assertNotNull(context.getBean(PicUploadService::class.java))
        }
    }

    @Test
    fun `pic upload is disabled when enable is false`() {
        runner.withPropertyValues("jmicro.pic.upload.enable=false")
            .run { context ->
                assertEquals(0, context.getBeansOfType(PicUploadConfigLoader::class.java).size)
                assertEquals(0, context.getBeansOfType(PicUploadService::class.java).size)
            }
    }
}
