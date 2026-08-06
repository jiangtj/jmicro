package com.jiangtj.micro.auth.oidc

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class OpenIDConnectAutoConfigurationTest {

    @Autowired
    lateinit var context: ApplicationContext

    @Test
    fun `test OpenIDConnectAutoConfiguration loads`() {
        val converter = context.getBean(JwtConverter::class.java)
        assertNotNull(converter)
    }

    @Test
    fun `test OidcLocator bean exists`() {
        val locator = context.getBean(OidcLocator::class.java)
        assertNotNull(locator)
    }
}
