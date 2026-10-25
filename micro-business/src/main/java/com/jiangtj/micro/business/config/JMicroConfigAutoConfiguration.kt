package com.jiangtj.micro.business.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean

@AutoConfiguration
@EnableConfigurationProperties(JMicroConfigProperties::class)
@ConditionalOnBooleanProperty(prefix = "jmicro.config", name = ["enabled"], havingValue = true)
class JMicroConfigAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun jmicroConfigSaver(): JMicroConfigSaver {
        return InMemoryJMicroConfigSaver()
    }

    @Bean
    fun jmicroConfigService(
        loaders: List<JMicroConfigLoader>,
        applicationEventPublisher: ApplicationEventPublisher,
        jmicroConfigProperties: JMicroConfigProperties,
        jmicroConfigSaver: JMicroConfigSaver,
    ): JMicroConfigService {
        return JMicroConfigService(loaders, applicationEventPublisher, jmicroConfigProperties, jmicroConfigSaver)
    }
}
