package com.jiangtj.micro.sql.jooq.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.jiangtj.micro.common.toJson
import org.jooq.JSON

class JSONDeserializer : JsonDeserializer<JSON>() {
    override fun deserialize(
        p: JsonParser,
        ctx: DeserializationContext
    ): JSON {
        val tree = p.codec.readTree(p) as JsonNode
        if (tree.isTextual) {
            return JSON.valueOf(tree.textValue())
        }
        return JSON.valueOf(tree.toJson())
    }
}