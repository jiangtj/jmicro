package com.jiangtj.micro.sql.jooq.jackson

import com.jiangtj.micro.common.toJson
import org.jooq.JSON
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ValueDeserializer

class JSONJackson3Deserializer : ValueDeserializer<JSON>() {

    override fun deserialize(
        p: JsonParser,
        ctx: DeserializationContext
    ): JSON {
        val tree = p.objectReadContext().readTree(p) as JsonNode
        if (tree.isString) {
            return JSON.valueOf(tree.stringValue())
        }
        return JSON.valueOf(tree.toJson())
    }
}