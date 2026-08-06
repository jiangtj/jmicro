package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Locator
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.ObjectProvider
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
    fun oidcLocator(jwtProperties: JwtProperties, oidcClients: ObjectProvider<OidcClient>, beanFactory: BeanFactory): OidcLocator {
        val clients = oidcClients.orderedStream().toList()
        val oidc = clients.ifEmpty { jwtProperties.oidc }
        val selfKeyService = findSelfKeyService(beanFactory)
        return OidcLocator(oidc, selfKeyService)
    }

    /**
     * 查找由外部（如 cas 模块）提供的本地验证密钥 [Locator]，排除 [OidcLocator] 自身以避免循环依赖。
     */
    private fun findSelfKeyService(beanFactory: BeanFactory): Locator<Key>? {
        if (beanFactory !is ListableBeanFactory) {
            return null
        }
        return beanFactory.getBeanNamesForType(Locator::class.java)
            .firstNotNullOfOrNull { name ->
                if (name == "oidcLocator") {
                    null
                } else {
                    beanFactory.getBean(name, Locator::class.java) as Locator<Key>?
                }
            }
    }
}
