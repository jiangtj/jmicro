package com.jiangtj.micro.business.pic.upload

import com.jiangtj.micro.business.config.SystemConfigAutoConfiguration
import com.jiangtj.micro.business.config.SystemConfigService
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * 图片上传自动配置
 *
 * 启动时注册 [PicUploadConfigLoader]，把通用配置与目录映射（`jmicro.pic.upload.*`）作为系统配置项加载；
 * 各上传服务商（local / ali / hw / minio / easyimages / rustfs）的配置同样由系统配置提供，
 * 其自动配置类在开启 `jmicro.config.enabled=true` 后生效。
 *
 * 可通过 `jmicro.pic.upload.enable=false` 关闭图片上传能力。
 */
@AutoConfiguration(after = [SystemConfigAutoConfiguration::class])
@EnableConfigurationProperties(PicUploadProperties::class)
@ConditionalOnBooleanProperty(prefix = "jmicro.pic.upload", name = ["enable"], havingValue = true, matchIfMissing = true)
class PicUploadAutoConfiguration {

    /**
     * 注册图片上传配置项，启动时加载
     */
    @Bean
    @ConditionalOnMissingBean
    fun picUploadConfigLoader(): PicUploadConfigLoader = PicUploadConfigLoader()

    /**
     * 注册图片上传服务
     */
    @Bean
    fun picUploadService(
        providers: List<PicUploadProvider>,
        systemConfigServiceProvider: ObjectProvider<SystemConfigService>,
    ): PicUploadService = PicUploadService(providers, systemConfigServiceProvider.getIfAvailable())
}
