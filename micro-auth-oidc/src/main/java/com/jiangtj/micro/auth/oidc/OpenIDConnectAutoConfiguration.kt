package com.jiangtj.micro.auth.oidc

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean


@AutoConfiguration
@EnableConfigurationProperties(WellKnownProperties::class)
class OpenIDConnectAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun openIDConnectJwtConverter(wellKnownProperties: WellKnownProperties): OpenIDConnectJwtConverter {
        return OpenIDConnectJwtConverter(wellKnownProperties)
    }

}
