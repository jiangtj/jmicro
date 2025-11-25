package com.jiangtj.micro.sql.jooq.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

object Jackson2WithKUtils {


    val objectMapper = ObjectMapper()

    init {
        objectMapper.registerModule(KotlinModule.Builder().build())
    }
}