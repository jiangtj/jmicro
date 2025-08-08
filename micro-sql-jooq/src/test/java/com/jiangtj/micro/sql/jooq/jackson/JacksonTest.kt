package com.jiangtj.micro.sql.jooq.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.JSON
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test

class JacksonTest {

    data class JSONExample(
        val json: JSON
    )

    @Test
    fun testNoJSONModule() {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule.Builder().build())
        assertThrows(MismatchedInputException::class.java) {
            mapper.readValue<JSONExample>("{\"json\":{\"name\":\"Jack\"}}")
        }
        // val module = JSONSimpleModule()
        val example = mapper.readValue<JSONExample>("{\"json\":\"{\\\"name\\\":\\\"Jack\\\"}\"}")
        assertEquals("{\"name\":\"Jack\"}", example.json.data())
        assertThrows(InvalidDefinitionException::class.java) {
            mapper.writeValueAsString(example)
        }
    }

    @Test
    fun testWithJSONModule() {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule.Builder().build())
        mapper.registerModule(JSONModule())
        val example = mapper.readValue<JSONExample>("{\"json\":\"{\\\"name\\\":\\\"Jack\\\"}\"}")
        assertEquals("{\"name\":\"Jack\"}", example.json.data())
        val example2 = mapper.readValue<JSONExample>("{\"json\":{\"name\":\"Jack\"}}")
        assertEquals("{\"name\":\"Jack\"}", example2.json.data())
        val text = mapper.writeValueAsString(example)
        assertEquals("{\"json\":{\"name\":\"Jack\"}}", text)
    }

}