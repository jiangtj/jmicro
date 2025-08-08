package com.jiangtj.micro.sql.jooq.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.JSON
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class JacksonTest {

    data class JSONExample(
        val json: JSON
    )

    @Test
    fun testJSON() {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule.Builder().build())
        assertThrows(MismatchedInputException::class.java) {
            mapper.readValue<JSONExample>("{\"json\":{\"name\":\"Jack\"}}")
        }
        val module = JSONSimpleModule()
        mapper.registerModule(module)
        val example = mapper.readValue<JSONExample>("{\"json\":\"{\\\"name\\\":\\\"Jack\\\"}\"}")
        assertEquals("{\"name\":\"Jack\"}", example.json.data())
        val example2 = mapper.readValue<JSONExample>("{\"json\":{\"name\":\"Jack\"}}")
        assertEquals("Jack", example2.json.data())
    }

}