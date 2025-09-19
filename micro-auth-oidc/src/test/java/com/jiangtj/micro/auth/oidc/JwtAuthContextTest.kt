package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Claims
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.mockito.kotlin.mock

class JwtAuthContextTest {

    @Test
    fun `test JwtAuthContext creation with all claims`() {
        val claims: Claims = mock()
        `when`(claims.subject).thenReturn("user123")
        `when`(claims.get("preferredUsername", String::class.java)).thenReturn("john_doe")
        `when`(claims.get("name", String::class.java)).thenReturn("John Doe")
        `when`(claims.get("picture", String::class.java)).thenReturn("https://example.com/avatar.jpg")
        `when`(claims.get("type", String::class.java)).thenReturn("user")
        `when`(claims.issuer).thenReturn("https://auth.example.com")

        val authContext = JwtAuthContext(claims)

        assertEquals("user123", authContext.subject().id)
        assertEquals("john_doe", authContext.subject().name)
        assertEquals("John Doe", authContext.subject().displayName)
        assertEquals("https://example.com/avatar.jpg", authContext.subject().avatar)
        assertEquals("user", authContext.subject().type)
        assertEquals("https://auth.example.com", authContext.subject().issuer)
    }

    @Test
    fun `test JwtAuthContext creation with minimal claims`() {
        val claims: Claims = mock()
        `when`(claims.subject).thenReturn("user456")
        `when`(claims.get("preferredUsername", String::class.java)).thenReturn(null)
        `when`(claims.get("name", String::class.java)).thenReturn(null)
        `when`(claims.get("picture", String::class.java)).thenReturn(null)
        `when`(claims.get("type", String::class.java)).thenReturn(null)
        `when`(claims.issuer).thenReturn("https://auth.example.com")

        val authContext = JwtAuthContext(claims)

        assertEquals("user456", authContext.subject().id)
        assertNull(authContext.subject().name)
        assertNull(authContext.subject().displayName)
        assertNull(authContext.subject().avatar)
        assertNull(authContext.subject().type)
        assertEquals("https://auth.example.com", authContext.subject().issuer)
    }

    @Test
    fun `test JwtAuthContext claims property`() {
        val claims: Claims = mock()
        `when`(claims.subject).thenReturn("user789")
        `when`(claims.issuer).thenReturn("https://auth.example.com")

        val authContext = JwtAuthContext(claims)

        assertEquals(claims, authContext.payload)
    }
}