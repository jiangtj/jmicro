package com.jiangtj.micro.sql.jooq.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.jooq.JSON

class JSONSerializer: JsonSerializer<JSON>() {
    override fun serialize(
        p0: JSON,
        p1: JsonGenerator,
        p2: SerializerProvider?
    ) {
        p1.writeString(p0.toString())
    }
}