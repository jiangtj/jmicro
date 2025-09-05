package com.jiangtj.micro.common.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ToMapTest {

    @Test
    fun toMap() {
        data class Test(
            val a: Int,
            val b: String
        )
        val test = Test(1, "a")
        val map = test.toMap()
        assertEquals(2, map.size)
        assertEquals(1, map["a"])
        assertEquals("a", map["b"])
    }

}