package com.jiangtj.micro.auth.oidc

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean


@AutoConfiguration
@EnableConfigurationProperties(OidcServerProperties::class)
@ConditionalOnBooleanProperty(prefix = "jmicro.oidc.server", name = ["enabled"], havingValue = true)
class OidcServerAutoConfiguration {

    @Bean
    fun oidcKeyService(oidcServerProperties: OidcServerProperties): OidcKeyService {
        return OidcKeyService(oidcServerProperties).apply {
            refreshKeys()
        }
    }

}
