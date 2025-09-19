package com.jiangtj.micro.auth.oidc

import com.jiangtj.micro.test.JMicroTest
import io.jsonwebtoken.Locator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource
import org.springframework.boot.context.properties.EnableConfigurationProperties

@JMicroTest
@SpringBootTest(classes = [OpenIDConnectAutoConfiguration::class])
@TestPropertySource(properties = [
    "micro.jwt.oidc[0].pattern=*",
    "micro.jwt.oidc[0].openid-configuration=https://example.com/.well-known/openid-configuration"
])
class OpenIDConnectAutoConfigurationTest {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `test JwtConverter bean is created`() {
        assertTrue(applicationContext.containsBean("openIDConnectJwtConverter"))
        val jwtConverter = applicationContext.getBean(JwtConverter::class.java)
        assertNotNull(jwtConverter)
    }

    @Test
    fun `test OidcLocator bean is created`() {
        assertTrue(applicationContext.containsBean("oidcLocator"))
        val oidcLocator = applicationContext.getBean(OidcLocator::class.java)
        assertNotNull(oidcLocator)
    }

    @Test
    fun `test JwtProperties bean is configured`() {
        val jwtProperties = applicationContext.getBean(JwtProperties::class.java)
        assertNotNull(jwtProperties)
        assertEquals(1, jwtProperties.oidc.size)
        assertEquals("*", jwtProperties.oidc[0].pattern)
        assertEquals("https://example.com/.well-known/openid-configuration", jwtProperties.oidc[0].openidConfiguration)
    }

    @Test
    fun `test all required beans are present`() {
        val requiredBeans = listOf(
            "openIDConnectJwtConverter",
            "oidcLocator"
        )
        
        requiredBeans.forEach { beanName ->
            assertTrue(applicationContext.containsBean(beanName), "Bean $beanName should be present")
        }
    }
}