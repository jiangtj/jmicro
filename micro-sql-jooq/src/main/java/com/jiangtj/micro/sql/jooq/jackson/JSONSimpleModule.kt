package com.jiangtj.micro.sql.jooq.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import org.jooq.JSON

class JSONSimpleModule: SimpleModule() {
    init {
        addDeserializer(JSON::class.java, JSONDeserializer())
        addSerializer(JSON::class.java, JSONSerializer())
    }
}