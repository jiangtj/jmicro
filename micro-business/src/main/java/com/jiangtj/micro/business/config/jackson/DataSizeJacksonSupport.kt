package com.jiangtj.micro.business.config.jackson

import org.springframework.util.unit.DataSize
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * [DataSize] 的 JSON 序列化器，输出字节数。
 */
class DataSizeSerializer : ValueSerializer<DataSize>() {

    override fun serialize(value: DataSize, gen: JsonGenerator, ctxt: SerializationContext) {
        gen.writeNumber(value.toBytes())
    }
}

/**
 * [DataSize] 的 JSON 反序列化器。
 *
 * 支持两种写法：
 * - 数字：按字节解释，如 `10485760`
 * - 字符串：使用 Spring 的宽松格式，如 `"10MB"`、`"1KB"`
 */
class DataSizeDeserializer : ValueDeserializer<DataSize>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DataSize {
        val node: JsonNode = p.objectReadContext().readTree(p)
        if (node.isNumber) {
            return DataSize.ofBytes(node.longValue())
        }
        return DataSize.parse(node.stringValue())
    }
}
