package com.jiangtj.micro.auth.oidc

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
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
            OidcProperties(
                pattern = "/api/**",
                openidConfiguration = "https://example.com/.well-known/openid-configuration"
            ),
            OidcProperties(
                pattern = "/admin/**",
                openidConfiguration = "https://admin.example.com/.well-known/openid-configuration"
            )
        )
        
        val properties = JwtProperties(oidc = oidcConfigs)
        
        assertEquals(2, properties.oidc.size)
        assertEquals("/api/**", properties.oidc[0].pattern)
        assertEquals("https://example.com/.well-known/openid-configuration", properties.oidc[0].openidConfiguration)
        assertEquals("/admin/**", properties.oidc[1].pattern)
        assertEquals("https://admin.example.com/.well-known/openid-configuration", properties.oidc[1].openidConfiguration)
    }

    @Test
    fun `test JwtProperties copy`() {
        val oidcConfigs = listOf(
            OidcProperties(
                pattern = "/api/**",
                openidConfiguration = "https://example.com/.well-known/openid-configuration"
            )
        )
        
        val original = JwtProperties(oidc = oidcConfigs)
        val copy = original.copy()
        
        assertEquals(original.oidc.size, copy.oidc.size)
        assertEquals(original.oidc[0].pattern, copy.oidc[0].pattern)
        assertEquals(original.oidc[0].openidConfiguration, copy.oidc[0].openidConfiguration)
    }
}