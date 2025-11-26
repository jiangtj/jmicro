package com.jiangtj.micro.sql.jooq.jackson

import org.jooq.JSON
import tools.jackson.databind.module.SimpleModule

class JSONJackson3Module: SimpleModule() {
    init {
        addDeserializer(JSON::class.java, JSONJackson3Deserializer())
        addSerializer(JSON::class.java, JSONJackson3Serializer())
    }
}