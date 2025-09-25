package com.jiangtj.micro.auth.oidc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JwtPropertiesTest {

    @Test
    fun `test default JwtProperties`() {
        val properties = JwtProperties()
        
        assertTrue(properties.oidc.isEmpty())
    }

    @Test
    fun `test JwtProperties with oidc configurations`() {
        val oidcConfigs = listOf(
            OidcProperties().apply {
                pattern = "/api/**"
                openidConfiguration = "https://example.com/.well-known/openid-configuration"
            },
            OidcProperties().apply {
                pattern = "/admin/**"
                openidConfiguration = "https://admin.example.com/.well-known/openid-configuration"
            }
        )
        
        val properties = JwtProperties().apply { oidc = oidcConfigs }
        
        assertEquals(2, properties.oidc.size)
        assertEquals("/api/**", properties.oidc[0].pattern)
        assertEquals("https://example.com/.well-known/openid-configuration", properties.oidc[0].openidConfiguration)
        assertEquals("/admin/**", properties.oidc[1].pattern)
        assertEquals("https://admin.example.com/.well-known/openid-configuration", properties.oidc[1].openidConfiguration)
    }

}