package com.jiangtj.micro.common.schema

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * [JSON Schema](https://json-schema.org/) 描述对象，实现了常用关键字的一个子集。
 *
 * 空字段在序列化时会被忽略，便于直接返回给前端使用。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class JsonSchema(
    @get:JsonProperty("\$schema")
    var schema: String? = null,
    var title: String? = null,
    var description: String? = null,
    var type: String? = null,
    var format: String? = null,
    var properties: MutableMap<String, JsonSchema>? = null,
    var required: MutableList<String>? = null,
    var items: JsonSchema? = null,
    var additionalProperties: JsonSchema? = null,
    @get:JsonProperty("enum")
    var enumValues: MutableList<Any>? = null,
    @get:JsonProperty("default")
    var defaultValue: Any? = null,
    var deprecated: Boolean? = null,
    var minimum: Number? = null,
    var maximum: Number? = null,
    var exclusiveMinimum: Number? = null,
    var exclusiveMaximum: Number? = null,
    var multipleOf: Number? = null,
    var minLength: Int? = null,
    var maxLength: Int? = null,
    var minItems: Int? = null,
    var maxItems: Int? = null,
    var pattern: String? = null,
)
