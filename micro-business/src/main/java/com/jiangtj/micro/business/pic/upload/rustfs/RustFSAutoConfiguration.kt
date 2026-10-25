package com.jiangtj.micro.business.pic.upload.rustfs

import com.jiangtj.micro.business.config.JMicroConfigAutoConfiguration
import com.jiangtj.micro.business.config.JMicroConfigService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import software.amazon.awssdk.services.s3.S3Client

/**
 * RustFS 自动配置，配置项来自系统配置（需开启 `jmicro.config.enabled=true`，
 * 并通过 `jmicro.rustfs.enable` 开启上传能力）
 */
@AutoConfiguration(after = [JMicroConfigAutoConfiguration::class])
@ConditionalOnClass(S3Client::class)
@ConditionalOnBean(JMicroConfigService::class)
class RustFSAutoConfiguration {

    /**
     * 注册 RustFS 配置项
     */
    @Bean
    @ConditionalOnMissingBean
    fun rustFSConfigLoader(): RustFSConfigLoader = RustFSConfigLoader()

    /**
     * 注册 RustFSService 服务
     */
    @Bean
    fun rustFSService(jmicroConfigService: JMicroConfigService): RustFSService {
        return RustFSService(jmicroConfigService)
    }
}
