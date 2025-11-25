package com.jiangtj.micro.sql.jooq.jackson

import com.jiangtj.micro.common.fromJson
import org.jooq.JSON
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

class JSONJackson3Serializer: ValueSerializer<JSON>() {
    override fun serialize(
        json: JSON,
        g: JsonGenerator,
        ctxt: SerializationContext?
    ) {
        g.writePOJO(json.data().fromJson<JsonNode>())
    }

}