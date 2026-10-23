package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigService
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * 图片上传自动配置类
 *
 * 各上传服务商（local / ali / hw / minio / easyimages / rustfs）的配置由系统配置提供，
 * 其自动配置类在开启 `jmicro.system.config.enabled=true` 后生效。
 */
@AutoConfiguration
@EnableConfigurationProperties(PicUploadProperties::class)
class PicUploadAutoConfiguration {

    /**
     * 注册图片上传服务
     */
    @Bean
    fun picUploadService(
        properties: PicUploadProperties,
        providers: List<PicUploadProvider>,
        systemConfigServiceProvider: ObjectProvider<SystemConfigService>,
    ): PicUploadService = PicUploadService(properties, providers, systemConfigServiceProvider.getIfAvailable())
}
