package com.jiangtj.micro.auth.oidc

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse


@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBooleanProperty(prefix = "jmicro.oidc.server", name = ["enabled"], havingValue = true)
class OidcServerServletAutoConfiguration {

    @Bean
    fun oidcEndpointService(
        oidcServerProperties: OidcServerProperties,
        oidcKeyService: OidcKeyService,
        oidcRedirectAuth: OidcRedirectAuth
    ): OidcEndpointService {
        return OidcEndpointService(oidcServerProperties, oidcKeyService, oidcRedirectAuth)
    }

    @Bean
    fun oidcEndpointRouterFunction(oidcEndpointService: OidcEndpointService): RouterFunction<ServerResponse> {
        return oidcEndpointService.endpoints()
    }

    @Bean
    @ConditionalOnMissingBean
    fun oidcRedirectAuth(): OidcRedirectAuth {
        return object : OidcRedirectAuth {
            override fun userInfo(): OidcEndpointService.UserInfo {
                TODO("Not yet implemented")
            }
        }
    }

}