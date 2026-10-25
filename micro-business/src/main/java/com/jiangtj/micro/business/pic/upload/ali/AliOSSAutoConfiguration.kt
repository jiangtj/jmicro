package com.jiangtj.micro.business.pic.upload.ali

import com.aliyun.oss.OSS
import com.jiangtj.micro.business.config.JMicroConfigAutoConfiguration
import com.jiangtj.micro.business.config.JMicroConfigService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * 阿里云 OSS 自动配置，配置项来自系统配置（需开启 `jmicro.config.enabled=true`）
 */
@AutoConfiguration(after = [JMicroConfigAutoConfiguration::class])
@ConditionalOnClass(OSS::class)
@ConditionalOnBean(JMicroConfigService::class)
class AliOSSAutoConfiguration {

    /**
     * 注册阿里云 OSS 配置项
     */
    @Bean
    @ConditionalOnMissingBean
    fun aliOSSConfigLoader(): AliOSSConfigLoader = AliOSSConfigLoader()

    /**
     * 注册阿里 oss 服务
     */
    @Bean
    fun aliOSSService(jmicroConfigService: JMicroConfigService): AliOSSService {
        return AliOSSService(jmicroConfigService)
    }
}
