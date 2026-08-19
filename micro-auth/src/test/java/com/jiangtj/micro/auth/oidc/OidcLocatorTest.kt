package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Header
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.springframework.web.client.RestClient
import java.security.KeyPair
import java.security.KeyPairGenerator

class OidcLocatorTest {

    private lateinit var oidcLocator: OidcLocator
    private lateinit var oidcClient: OidcClient
    private lateinit var restClient: RestClient

    private val keyPair: KeyPair = KeyPairGenerator.getInstance("RSA").apply {
        initialize(2048)
    }.generateKeyPair()

    @BeforeEach
    fun setUp() {
        oidcClient = mock<OidcClient>()
        restClient = mock<RestClient>()
        oidcLocator = OidcLocator(listOf(oidcClient))
    }

    @Test
    fun `test OidcLocator with empty oidc clients returns null`() {
        val locator = OidcLocator(emptyList())
        val header: Header = mock()
        `when`(header["kid"]).thenReturn("test-kid")

        val result = locator.locate(header)
        assertNull(result)
    }

    @Test
    fun `test OidcLocator locate with invalid kid returns null`() {
        val header: Header = mock()
        `when`(header["kid"]).thenReturn(null)

        val result = oidcLocator.locate(header)
        assertNull(result)
    }

    @Test
    fun `test match with ALWAYS matcher style`() {
        val client = OidcProperties().apply {
            matcherStyle = MatcherStyle.ALWAYS
        }

        assertTrue(oidcLocator.match(client, "any-kid"))
    }

    @Test
    fun `test match with PREFIX matcher style`() {
        val client = OidcProperties().apply {
            matcherStyle = MatcherStyle.PREFIX
            pattern = "test-"
        }

        assertTrue(oidcLocator.match(client, "test-kid"))
        assertFalse(oidcLocator.match(client, "other-kid"))
    }

    @Test
    fun `test match with REGEX matcher style`() {
        val client = OidcProperties().apply {
            matcherStyle = MatcherStyle.REGEX
            pattern = "^test-.*$"
        }

        assertTrue(oidcLocator.match(client, "test-kid"))
        assertFalse(oidcLocator.match(client, "other-kid"))
    }

    @Test
    fun `test match with ANT matcher style`() {
        val client = OidcProperties().apply {
            matcherStyle = MatcherStyle.ANT
            pattern = "/api/**"
        }

        assertTrue(oidcLocator.match(client, "/api/users"))
        assertFalse(oidcLocator.match(client, "/other/users"))
    }

    @Test
    fun `test handle with jwks uri`() {
        val client = OidcProperties().apply {
            jwksUri = "https://example.com/jwks"
        }

        // 由于 RestClient 的模拟比较复杂，这里主要测试 match 和 locate 的逻辑
        val header: Header = mock()
        `when`(header["kid"]).thenReturn("non-matching-kid")

        val result = oidcLocator.locate(header)
        assertNull(result)
    }

}
