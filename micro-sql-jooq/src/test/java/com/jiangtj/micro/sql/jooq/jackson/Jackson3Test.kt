package com.jiangtj.micro.sql.jooq.jackson

import org.jooq.JSON
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import tools.jackson.databind.exc.MismatchedInputException
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import tools.jackson.module.kotlin.readValue
import kotlin.test.Test

class Jackson3Test {

    data class JSONExample(
        val json: JSON
    )

    @Test
    fun testNoJSONModule() {
        val mapper = JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .build()

        assertThrows(MismatchedInputException::class.java) {
            mapper.readValue<JSONExample>("{\"json\":{\"name\":\"Jack\"}}")
        }
        // val module = JSONSimpleModule()
        val example = mapper.readValue<JSONExample>("{\"json\":\"{\\\"name\\\":\\\"Jack\\\"}\"}")
        assertEquals("{\"name\":\"Jack\"}", example.json.data())
        assertEquals("{\"json\":{}}", mapper.writeValueAsString(example))
    }

    @Test
    fun testWithJSONModule() {
        val mapper = JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .addModule(JSONJackson3Module())
            .build()
        val example = mapper.readValue<JSONExample>("{\"json\":\"{\\\"name\\\":\\\"Jack\\\"}\"}")
        assertEquals("{\"name\":\"Jack\"}", example.json.data())
        val example2 = mapper.readValue<JSONExample>("{\"json\":{\"name\":\"Jack\"}}")
        assertEquals("{\"name\":\"Jack\"}", example2.json.data())
        val text = mapper.writeValueAsString(example)
        assertEquals("{\"json\":{\"name\":\"Jack\"}}", text)
    }

}