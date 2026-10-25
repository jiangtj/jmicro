package com.jiangtj.micro.business.pic.upload.hw

import com.jiangtj.micro.business.config.SystemConfigAutoConfiguration
import com.jiangtj.micro.business.config.SystemConfigService
import com.obs.services.ObsClient
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * 华为云 OBS 自动配置，配置项来自系统配置（需开启 `jmicro.config.enabled=true`）
 */
@AutoConfiguration(after = [SystemConfigAutoConfiguration::class])
@ConditionalOnClass(ObsClient::class)
@ConditionalOnBean(SystemConfigService::class)
class ObsAutoConfiguration {

    /**
     * 注册华为云 OBS 配置项
     */
    @Bean
    @ConditionalOnMissingBean
    fun obsConfigLoader(): ObsConfigLoader = ObsConfigLoader()

    /**
     * 注册 hwcloud obs 服务
     */
    @Bean
    fun obsService(systemConfigService: SystemConfigService): ObsService {
        return ObsService(systemConfigService)
    }
}
