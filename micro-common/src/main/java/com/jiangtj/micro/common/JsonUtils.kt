package com.jiangtj.micro.common

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import lombok.extern.slf4j.Slf4j
import java.io.IOException


@Slf4j
object JsonUtils {
    private var objectMapper: ObjectMapper

    init {
        objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule()) //处理 java8 time api
        objectMapper.registerModule(KotlinModule.Builder().build()) // 添加 kotlin 支持

        // 取消时间的转化格式,默认是时间戳,可以取消
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        //如果是空对象的时候,不抛异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    }

    // 使用 Spring boot 容器中的 ObjectMapper 代替默认
    @JvmStatic
    fun init(om: ObjectMapper) {
        objectMapper = om.copy()
    }

    @JvmStatic
    fun toJson(`object`: Any?): String? {
        if (`object` == null) {
            return null
        }

        var json: String? = null

        try {
            json = objectMapper.writeValueAsString(`object`)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return json
    }

    @JvmStatic
    fun <T> fromJson(json: String?, classType: Class<T>): T? {
        if (json == null) {
            return null
        }

        var `object`: T? = null

        try {
            `object` = objectMapper.readValue<T>(json, classType)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return `object`
    }

    @JvmStatic
    fun <T> getListFromJson(json: String?, classType: Class<T>): MutableList<T>? {
        if (json == null) {
            return null
        }

        var lists: MutableList<T>? = null

        try {
            val javaType: JavaType? =
                objectMapper.getTypeFactory().constructCollectionType(ArrayList::class.java, classType)
            lists = objectMapper.readValue<MutableList<T>>(json, javaType)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return lists
    }
}
