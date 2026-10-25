package com.jiangtj.micro.business.pic.upload.minio

import com.jiangtj.micro.business.config.JMicroConfigAutoConfiguration
import com.jiangtj.micro.business.config.JMicroConfigService
import io.minio.MinioClient
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * MinIO 自动配置，配置项来自系统配置（需开启 `jmicro.config.enabled=true`）
 */
@AutoConfiguration(after = [JMicroConfigAutoConfiguration::class])
@ConditionalOnClass(MinioClient::class)
@ConditionalOnBean(JMicroConfigService::class)
class MinIOAutoConfiguration {

    /**
     * 注册 MinIO 配置项
     */
    @Bean
    @ConditionalOnMissingBean
    fun minIOConfigLoader(): MinIOConfigLoader = MinIOConfigLoader()

    /**
     * 注册 MinIOService 服务
     */
    @Bean
    fun minioService(jmicroConfigService: JMicroConfigService): MinIOService {
        return MinIOService(jmicroConfigService)
    }
}
