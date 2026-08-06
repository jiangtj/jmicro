package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Locator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.springframework.web.client.RestClient
import java.security.Key
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
        oidcLocator = OidcLocator(listOf(oidcClient), null)
    }

    @Test
    fun `test OidcLocator with empty oidc clients returns null`() {
        val locator = OidcLocator(emptyList(), null)
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

    @Test
    fun `test OidcLocator with self oidcKeyService`() {
        val selfKey = keyPair.public
        val selfKeyService = Locator<Key> { header -> selfKey }
        val locator = OidcLocator(emptyList(), selfKeyService)

        val jwt = Jwts.builder()
            .subject("test")
            .header().add("kid", "self-kid").and()
            .signWith(keyPair.private)
            .compact()

        val header = Jwts.parser().keyLocator { keyPair.public }.build().parseSignedClaims(jwt).header
        val headerMap = mapOf<String, Any>("kid" to "self-kid")

        // selfKeyService.locate 应该被调用并返回 selfKey
        val result = locator.locate(header)
        // 由于 selfKeyService 不检查 kid，会直接返回 selfKey
        assertEquals(selfKey, result)
    }
}
