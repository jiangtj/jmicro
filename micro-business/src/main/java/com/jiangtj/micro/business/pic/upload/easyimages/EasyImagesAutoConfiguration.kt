package com.jiangtj.micro.business.pic.upload.easyimages

import com.jiangtj.micro.business.config.SystemConfigAutoConfiguration
import com.jiangtj.micro.business.config.SystemConfigService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * EasyImages 自动配置，配置项来自系统配置（需开启 `jmicro.config.enabled=true`，
 * 且配置 `easyimages.api.url` 后才可正常上传）
 */
@AutoConfiguration(after = [SystemConfigAutoConfiguration::class])
@ConditionalOnBean(SystemConfigService::class)
class EasyImagesAutoConfiguration {

    /**
     * 注册 EasyImages 配置项
     */
    @Bean
    @ConditionalOnMissingBean
    fun easyImagesConfigLoader(): EasyImagesConfigLoader = EasyImagesConfigLoader()

    /**
     * 注册 EasyImages 服务
     */
    @Bean
    fun easyImagesService(systemConfigService: SystemConfigService): EasyImagesService {
        return EasyImagesService(systemConfigService)
    }
}
