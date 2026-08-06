package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.springframework.core.Ordered
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator

class KidLocatorTest {

    private val keyPair: KeyPair = KeyPairGenerator.getInstance("RSA").apply {
        initialize(2048)
    }.generateKeyPair()

    private val privateKey: Key = keyPair.private
    private val publicKey: Key = keyPair.public

    @Test
    fun `test KidOrderedLocator default order`() {
        val locator = object : KidOrderedLocator {
            override fun support(kid: String): Boolean = true
            override fun handle(header: Header): Key = publicKey
        }
        
        assertEquals(0, locator.order)
        assertTrue(locator is Ordered)
    }

    @Test
    fun `test KidLocator locate with matching kid`() {
        val kid = "test-kid"
        
        val locator = object : KidLocator {
            override fun support(kid: String): Boolean = kid == "test-kid"
            override fun handle(header: Header): Key = publicKey
        }

        val header: Header = mock()
        `when`(header["kid"]).thenReturn(kid)

        val result = locator.locate(header)
        assertNotNull(result)
        assertEquals(publicKey, result)
    }

    @Test
    fun `test KidLocator locate with non-matching kid`() {
        val locator = object : KidLocator {
            override fun support(kid: String): Boolean = kid == "test-kid"
            override fun handle(header: Header): Key = publicKey
        }

        val header: Header = mock()
        `when`(header["kid"]).thenReturn("different-kid")

        val result = locator.locate(header)
        assertNull(result)
    }

    @Test
    fun `test KidLocator locate with null kid`() {
        val locator = object : KidLocator {
            override fun support(kid: String): Boolean = true
            override fun handle(header: Header): Key = publicKey
        }

        val header: Header = mock()
        `when`(header["kid"]).thenReturn(null)

        val result = locator.locate(header)
        assertNull(result)
    }

    @Test
    fun `test KidLocator with actual JWT token`() {
        val kid = "jwt-kid"
        
        val jwt = Jwts.builder()
            .subject("test-user")
            .header().add("kid", kid).and()
            .signWith(privateKey)
            .compact()

        val header = Jwts.parser().keyLocator { publicKey }.build().parseSignedClaims(jwt).header

        val locator = object : KidLocator {
            override fun support(kid: String): Boolean = kid == "jwt-kid"
            override fun handle(header: Header): Key = publicKey
        }

        val result = locator.locate(header)
        assertNotNull(result)
        assertEquals(publicKey, result)
    }

    @Test
    fun `test KidLocator support method is called correctly`() {
        val locator = object : KidLocator {
            override fun support(kid: String): Boolean {
                assertEquals("check-kid", kid)
                return true
            }
            override fun handle(header: Header): Key = publicKey
        }

        val header: Header = mock()
        `when`(header["kid"]).thenReturn("check-kid")

        locator.locate(header)
    }
}
