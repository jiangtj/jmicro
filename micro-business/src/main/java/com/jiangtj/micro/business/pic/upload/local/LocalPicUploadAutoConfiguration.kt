package com.jiangtj.micro.business.pic.upload.local

import com.jiangtj.micro.business.config.JMicroConfigAutoConfiguration
import com.jiangtj.micro.business.config.JMicroConfigService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * 本地上传自动配置，配置项来自系统配置（需开启 `jmicro.config.enabled=true`）
 */
@AutoConfiguration(after = [JMicroConfigAutoConfiguration::class])
@ConditionalOnBean(JMicroConfigService::class)
class LocalPicUploadAutoConfiguration {

    /**
     * 注册本地上传配置项
     */
    @Bean
    @ConditionalOnMissingBean
    fun localPicUploadConfigLoader(): LocalPicUploadConfigLoader = LocalPicUploadConfigLoader()

    /**
     * 注册本地图片上传供应商
     */
    @Bean
    fun localPicUploadService(jmicroConfigService: JMicroConfigService): LocalPicUploadService {
        return LocalPicUploadService(jmicroConfigService)
    }
}
