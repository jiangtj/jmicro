package com.jiangtj.micro.auth.oidc

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean


@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBooleanProperty(prefix = "micro.oidc.server", name = ["enabled"], havingValue = true)
class OidcServerServletAutoConfiguration {

    @Bean
    fun oidcEndpointService(oidcServerProperties: OidcServerProperties): OidcEndpointService {
        return OidcEndpointService(oidcServerProperties)
    }

}
