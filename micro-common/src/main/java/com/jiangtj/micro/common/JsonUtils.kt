package com.jiangtj.micro.common

import com.jiangtj.micro.common.JsonUtils.jsonMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import lombok.extern.slf4j.Slf4j
import tools.jackson.core.JacksonException
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import tools.jackson.module.kotlin.readValue
import java.io.IOException

@Slf4j
object JsonUtils {
    val log = KotlinLogging.logger {}
    var jsonMapper: JsonMapper

    init {
        jsonMapper = JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .build()
    }

    /**
     * 初始化 JsonUtils 中的 ObjectMapper 实例。
     * 该方法会使用传入的 ObjectMapper 实例的副本替换当前的 ObjectMapper 实例，
     * 以确保后续的 JSON 序列化和反序列化操作使用新的配置。
     *
     * @param om 用于初始化的 ObjectMapper 实例。
     */
    @JvmStatic
    fun init(mapper: JsonMapper) {
        jsonMapper = mapper
    }

    /**
     * 将对象转换为 JSON 字符串。
     * 该方法接收一个任意类型的对象，使用预配置的 ObjectMapper 将其序列化为 JSON 字符串。
     * 如果传入的对象为 null，则直接返回 null；若序列化过程中出现异常，会记录错误日志并返回 null。
     *
     * @param obj 需要转换为 JSON 字符串的对象。
     * @return 转换后的 JSON 字符串，若对象为 null 或序列化失败则返回 null。
     */
    @JvmStatic
    fun toJson(obj: Any?): String? {
        if (obj == null) {
            return null
        }

        try {
            return jsonMapper.writeValueAsString(obj)
        } catch (e: JacksonException) {
            log.error(e) { "JsonUtils error" }
        }
        return null
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的对象。
     * 该方法使用预配置的 ObjectMapper 对 JSON 字符串进行反序列化。
     * 如果传入的 JSON 字符串为 null，则直接返回 null；若反序列化过程中出现异常，会记录错误日志并返回 null。
     *
     * @param json 需要反序列化的 JSON 字符串，若为 null 则直接返回 null。
     * @param classType 反序列化目标对象的 Class 类型。
     * @param T 反序列化目标对象的类型。
     * @return 反序列化后的对象，若 JSON 字符串为 null 或反序列化失败则返回 null。
     */
    @JvmStatic
    fun <T> fromJson(json: String?, classType: Class<T>): T? {
        if (json == null) {
            return null
        }

        try {
            return jsonMapper.readValue<T>(json, classType)
        } catch (e: JacksonException) {
            log.error(e) { "JsonUtils error" }
        }

        return null
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的可变列表对象。
     * 该方法使用预配置的 ObjectMapper 对 JSON 字符串进行反序列化，构建一个包含指定类型元素的可变列表。
     * 如果传入的 JSON 字符串为 null，则直接返回 null；若反序列化过程中出现异常，会记录错误日志并返回 null。
     *
     * @param json 需要反序列化的 JSON 字符串，若为 null 则直接返回 null。
     * @param classType 反序列化目标列表中元素的 Class 类型。
     * @param T 反序列化目标列表中元素的类型。
     * @return 反序列化后的可变列表对象，若 JSON 字符串为 null 或反序列化失败则返回 null。
     */
    @JvmStatic
    fun <T> getListFromJson(json: String?, classType: Class<T>): MutableList<T>? {
        if (json == null) {
            return null
        }

        try {
            val javaType = jsonMapper.typeFactory.constructCollectionType(ArrayList::class.java, classType)
            return jsonMapper.readValue<MutableList<T>>(json, javaType)
        } catch (e: IOException) {
            log.error(e) { "JsonUtils error" }
        }

        return null
    }

    /**
     * 使用 Kotlin 的 reified 特性，将 JSON 字符串反序列化为指定类型的对象。
     *
     * @param json 需要反序列化的 JSON 字符串，若为 null 则直接返回 null。
     * @param T 反序列化目标对象的类型。
     * @return 反序列化后的对象，若 JSON 字符串为 null 或反序列化失败则返回 null。
     */
    inline fun <reified T> fromJson(json: String?): T? {
        if (json == null) {
            return null
        }

        try {
            return jsonMapper.readValue<T>(json)
        } catch (e: IOException) {
            log.error(e) { "JsonUtils error" }
        }

        return null
    }
}

/**
 * 扩展函数：将对象序列化为JSON字符串
 * 使用预配置的ObjectMapper实例进行序列化操作
 * @return 序列化后的JSON字符串
 * @throws JsonProcessingException 当序列化过程中发生错误时抛出
 */
fun <T> T.toJson(): String = jsonMapper.writeValueAsString(this)

/**
 * 内联扩展函数：将JSON字符串反序列化为指定类型的对象
 * 使用Kotlin的reified泛型特性，无需显式传入Class对象
 * @return 反序列化后的指定类型对象
 * @throws IOException 当反序列化过程中发生错误时抛出
 */
inline fun <reified T> String.fromJson() = jsonMapper.readValue<T>(this)
