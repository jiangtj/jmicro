package com.jiangtj.micro.business.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean

@AutoConfiguration
@EnableConfigurationProperties(SystemConfigProperties::class)
@ConditionalOnBooleanProperty(prefix = "jmicro.system.config", name = ["enabled"], havingValue = true)
class SystemConfigAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun systemConfigSaver(): SystemConfigSaver {
        return InMemorySystemConfigSaver()
    }

    @Bean
    fun systemConfigService(
        loaders: List<SystemConfigLoader>,
        applicationEventPublisher: ApplicationEventPublisher,
        systemConfigProperties: SystemConfigProperties,
        systemConfigSaver: SystemConfigSaver,
    ): SystemConfigService {
        return SystemConfigService(loaders, applicationEventPublisher, systemConfigProperties, systemConfigSaver)
    }
}
