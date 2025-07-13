package com.jiangtj.micro.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import java.io.Serializable

private val log = KotlinLogging.logger {}

class JsonUtilsKtTest {

    data class Pair2(val first: Int, val second: String): Serializable

    @Test
    fun toJson() {
        val text = JsonUtils.toJson(Pair2(1, "2"))
        log.info { text }
    }
    @Test
    fun fromJson() {
        val fromJson = JsonUtils.fromJson("{\"first\":1,\"second\":\"2\"}", Pair2::class.java)
        log.info { fromJson }
    }

    @Test
    fun getListFromJson() {
        val fromJson = JsonUtils.getListFromJson("[{\"first\":1,\"second\":\"2\"},{\"first\":3,\"second\":\"x\"}]", Pair2::class.java)
        log.info { fromJson }
    }

}