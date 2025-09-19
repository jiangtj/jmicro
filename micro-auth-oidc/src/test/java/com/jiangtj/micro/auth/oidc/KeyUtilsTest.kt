package com.jiangtj.micro.auth.oidc

import io.jsonwebtoken.Header
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.mockito.kotlin.mock

class KeyUtilsTest {

    @Test
    fun `test getKid with existing kid`() {
        val header: Header = mock()
        `when`(header["kid"]).thenReturn("test-key-id")

        val kid = header.getKid()

        assertEquals("test-key-id", kid)
    }

    @Test
    fun `test getKid with null kid`() {
        val header: Header = mock()
        `when`(header["kid"]).thenReturn(null)

        val kid = header.getKid()

        assertNull(kid)
    }

    @Test
    fun `test getKid with non-string kid`() {
        val header: Header = mock()
        `when`(header["kid"]).thenReturn(12345) // 非字符串值

        val kid = header.getKid()

        assertNull(kid) // 由于类型转换失败，应该返回 null
    }

    @Test
    fun `test getKid with empty string kid`() {
        val header: Header = mock()
        `when`(header["kid"]).thenReturn("")

        val kid = header.getKid()

        assertEquals("", kid)
    }
}