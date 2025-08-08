package com.jiangtj.micro.sql.jooq.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer
import com.jiangtj.micro.common.toJson
import org.jooq.JSON

class JSONDeserializer : JsonDeserializer<JSON>() {
    override fun deserialize(
        p: JsonParser,
        ctx: DeserializationContext
    ): JSON {
//        val deserializer = JsonNodeDeserializer.getDeserializer(JsonNode::class.java)
//        val nodes = deserializer.deserialize(p, ctx)
        return JSON.valueOf(p.text)
    }
}