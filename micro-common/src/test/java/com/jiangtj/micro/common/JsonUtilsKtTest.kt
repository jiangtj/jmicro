package com.jiangtj.micro.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import java.io.Serializable
import kotlin.test.assertEquals

private val log = KotlinLogging.logger {}

class JsonUtilsKtTest {

    data class Pair2(val first: Int, val second: String): Serializable

    data class Generic<T>(val data: T)

    @Test
    fun toJson() {
        val obj = Pair2(1, "2")
        val text = JsonUtils.toJson(obj)
        assertEquals("{\"first\":1,\"second\":\"2\"}", text)
        assertEquals("{\"first\":1,\"second\":\"2\"}", obj.toJson())
    }

    @Test
    fun fromJson() {
        val fromJson = JsonUtils.fromJson("{\"first\":1,\"second\":\"2\"}", Pair2::class.java)
        assertEquals(1, fromJson?.first)
        assertEquals("2", fromJson?.second)
        val fromJson2 = JsonUtils.fromJson<Pair2>("{\"first\":1,\"second\":\"2\"}")
        assertEquals(1, fromJson2?.first)
        assertEquals("2", fromJson2?.second)
        val fromJson3 = "{\"first\":1,\"second\":\"2\"}".fromJson<Pair2>()
        assertEquals(1, fromJson3.first)
        assertEquals("2", fromJson3.second)
    }

    @Test
    fun getListFromJson() {
        val fromJson = JsonUtils.getListFromJson("[{\"first\":1,\"second\":\"2\"},{\"first\":3,\"second\":\"x\"}]", Pair2::class.java)
        assertEquals(2, fromJson?.size)
        assertEquals(1, fromJson?.first()?.first)
        assertEquals("2", fromJson?.first()?.second)
        val fromJson2 = JsonUtils.fromJson<List<Pair2>>("[{\"first\":1,\"second\":\"2\"},{\"first\":3,\"second\":\"x\"}]")
        assertEquals(2, fromJson2?.size)
        assertEquals(1, fromJson2?.first()?.first)
        assertEquals("2", fromJson2?.first()?.second)
        val fromJson3 = "[{\"first\":1,\"second\":\"2\"},{\"first\":3,\"second\":\"x\"}]".fromJson<List<Pair2>>()
        assertEquals(2, fromJson3.size)
        assertEquals(1, fromJson3.first().first)
        assertEquals("2", fromJson3.first().second)
    }

    @Test
    fun testGeneric() {
        val generic = JsonUtils.fromJson<Generic<Pair2>>("{\"data\":{\"first\":1,\"second\":\"2\"}}")
        assertEquals(1, generic?.data?.first)
        assertEquals("2", generic?.data?.second)
        val generic2 = "{\"data\":{\"first\":1,\"second\":\"2\"}}".fromJson<Generic<Pair2>>()
        assertEquals(1, generic2.data.first)
        assertEquals("2", generic2.data.second)
    }

}