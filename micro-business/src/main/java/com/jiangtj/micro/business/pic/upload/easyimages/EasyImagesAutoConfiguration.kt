package com.jiangtj.micro.business.pic.upload.easyimages

import com.jiangtj.micro.business.config.JMicroConfigAutoConfiguration
import com.jiangtj.micro.business.config.JMicroConfigService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * EasyImages 自动配置，配置项来自系统配置（需开启 `jmicro.config.enabled=true`，
 * 且配置 `easyimages.api.url` 后才可正常上传）
 */
@AutoConfiguration(after = [JMicroConfigAutoConfiguration::class])
@ConditionalOnBean(JMicroConfigService::class)
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
    fun easyImagesService(jmicroConfigService: JMicroConfigService): EasyImagesService {
        return EasyImagesService(jmicroConfigService)
    }
}
