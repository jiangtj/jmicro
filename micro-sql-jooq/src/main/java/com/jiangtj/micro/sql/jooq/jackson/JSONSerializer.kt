package com.jiangtj.micro.sql.jooq.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.JSON

class JSONSerializer: JsonSerializer<JSON>() {
    override fun serialize(
        json: JSON,
        g: JsonGenerator,
        provider: SerializerProvider?
    ) {
        g.writeObject(Jackson2WithKUtils.objectMapper.readValue<JsonNode>(json.data()))
    }
}