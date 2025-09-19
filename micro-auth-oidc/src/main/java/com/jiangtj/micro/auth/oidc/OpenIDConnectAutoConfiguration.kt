package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Locator
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import java.security.Key


@AutoConfiguration
@EnableConfigurationProperties(JwtProperties::class)
class OpenIDConnectAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun openIDConnectJwtConverter(locators: List<Locator<out Key>>): JwtConverter {
        return JwtConverter(locators)
    }

    @Bean
    fun oidcLocator(jwtProperties: JwtProperties): OidcLocator {
        return OidcLocator(jwtProperties)
    }

}
