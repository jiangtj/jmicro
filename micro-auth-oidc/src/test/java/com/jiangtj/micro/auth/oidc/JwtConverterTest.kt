package com.jiangtj.micro.auth.oidc

import com.jiangtj.micro.auth.context.AuthContext
import com.jiangtj.micro.auth.context.AuthRequest
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.springframework.test.util.ReflectionTestUtils
import java.security.Key
import java.util.*
import javax.crypto.SecretKey

class JwtConverterTest {

    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    
    private val testLocator = object : Locator<Key> {
        override fun locate(header: Header): Key? {
            return if (header.getKid() == "test-key") secretKey else null
        }
    }

    @Test
    fun `test convert with valid JWT token`() {
        // 创建有效的 JWT token
        val now = Date()
        val expiry = Date(now.time + 3600000) // 1小时后过期
        
        val jwt = Jwts.builder()
            .setSubject("user123")
            .setIssuer("https://auth.example.com")
            .setIssuedAt(now)
            .setExpiration(expiry)
            .claim("preferredUsername", "john_doe")
            .claim("name", "John Doe")
            .claim("picture", "https://example.com/avatar.jpg")
            .claim("type", "user")
            .setHeaderParam("kid", "test-key")
            .signWith(secretKey)
            .compact()

        val authRequest: AuthRequest = mock()
        `when`(authRequest.getHeaders("Authorization")).thenReturn(listOf("Bearer $jwt"))

        val converter = JwtConverter(listOf(testLocator))
        val authContext = converter.convert(authRequest)

        assertNotNull(authContext)
        assertTrue(authContext is JwtAuthContext)
        assertEquals("user123", authContext.subject().id)
        assertEquals("john_doe", authContext.subject().name)
        assertEquals("John Doe", authContext.subject().displayName)
        assertEquals("https://example.com/avatar.jpg", authContext.subject().avatar)
        assertEquals("user", authContext.subject().type)
        assertEquals("https://auth.example.com", authContext.subject().issuer)
    }

    @Test
    fun `test convert with invalid JWT token`() {
        val authRequest: AuthRequest = mock()
        `when`(authRequest.getHeaders("Authorization")).thenReturn(listOf("Bearer invalid.jwt.token"))

        val converter = JwtConverter(listOf(testLocator))
        val authContext = converter.convert(authRequest)

        assertNotNull(authContext)
        assertFalse(authContext.isLogin())
    }

    @Test
    fun `test convert with no Authorization header`() {
        val authRequest: AuthRequest = mock()
        `when`(authRequest.getHeaders("Authorization")).thenReturn(emptyList())

        val converter = JwtConverter(listOf(testLocator))
        val authContext = converter.convert(authRequest)

        assertNotNull(authContext)
        assertFalse(authContext.isLogin())
    }

    @Test
    fun `test convert with multiple Authorization headers`() {
        val authRequest: AuthRequest = mock()
        `when`(authRequest.getHeaders("Authorization")).thenReturn(listOf("Bearer token1", "Bearer token2"))

        val converter = JwtConverter(listOf(testLocator))
        val authContext = converter.convert(authRequest)

        assertNotNull(authContext)
        assertFalse(authContext.isLogin())
    }

    @Test
    fun `test convert with JWT token but no matching key`() {
        // 创建 JWT token，但使用不匹配的 kid
        val now = Date()
        val expiry = Date(now.time + 3600000)
        
        val jwt = Jwts.builder()
            .setSubject("user123")
            .setIssuer("https://auth.example.com")
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setHeaderParam("kid", "unknown-key")
            .signWith(secretKey)
            .compact()

        val authRequest: AuthRequest = mock()
        `when`(authRequest.getHeaders("Authorization")).thenReturn(listOf("Bearer $jwt"))

        val converter = JwtConverter(listOf(testLocator))
        val authContext = converter.convert(authRequest)

        assertNotNull(authContext)
        assertFalse(authContext.isLogin())
    }

    @Test
    fun `test convert with expired JWT token`() {
        // 创建已过期的 JWT token
        val now = Date()
        val expiry = Date(now.time - 3600000) // 1小时前过期
        
        val jwt = Jwts.builder()
            .setSubject("user123")
            .setIssuer("https://auth.example.com")
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setHeaderParam("kid", "test-key")
            .signWith(secretKey)
            .compact()

        val authRequest: AuthRequest = mock()
        `when`(authRequest.getHeaders("Authorization")).thenReturn(listOf("Bearer $jwt"))

        val converter = JwtConverter(listOf(testLocator))
        val authContext = converter.convert(authRequest)

        assertNotNull(authContext)
        assertFalse(authContext.isLogin())
    }
}