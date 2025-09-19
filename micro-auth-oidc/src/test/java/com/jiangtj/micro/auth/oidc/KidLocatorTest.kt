package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Header
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import java.security.Key

class KidLocatorTest {

    private val testKidLocator = object : KidLocator {
        override fun support(kid: String): Boolean {
            return kid.startsWith("test-")
        }

        override fun handle(header: Header): Key {
            return mock(Key::class.java)
        }
    }

    @Test
    fun `test locate with matching kid`() {
        val header: Header = mock()
        `when`(header.getKid()).thenReturn("test-key123")

        val result = testKidLocator.locate(header)

        assertNotNull(result)
        assertTrue(result is Key)
    }

    @Test
    fun `test locate with non-matching kid`() {
        val header: Header = mock()
        `when`(header.getKid()).thenReturn("prod-key123")

        val result = testKidLocator.locate(header)

        assertNull(result)
    }

    @Test
    fun `test locate with null kid`() {
        val header: Header = mock()
        `when`(header.getKid()).thenReturn(null)

        val result = testKidLocator.locate(header)

        assertNull(result)
    }

    @Test
    fun `test support method`() {
        assertTrue(testKidLocator.support("test-key123"))
        assertTrue(testKidLocator.support("test-abc"))
        assertFalse(testKidLocator.support("prod-key123"))
        assertFalse(testKidLocator.support("dev-key123"))
        assertFalse(testKidLocator.support(""))
    }
}