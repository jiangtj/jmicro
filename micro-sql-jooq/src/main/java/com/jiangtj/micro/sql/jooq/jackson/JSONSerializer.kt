package com.jiangtj.micro.sql.jooq.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.jiangtj.micro.common.fromJson
import org.jooq.JSON

class JSONSerializer: JsonSerializer<JSON>() {
    override fun serialize(
        json: JSON,
        g: JsonGenerator,
        provider: SerializerProvider?
    ) {
        g.writeObject(json.data().fromJson<JsonNode>())
    }
}