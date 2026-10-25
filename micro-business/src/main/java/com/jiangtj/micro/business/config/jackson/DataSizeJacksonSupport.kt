package com.jiangtj.micro.business.config.jackson

import org.springframework.util.unit.DataSize
import org.springframework.util.unit.DataUnit
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer

/**
 * [DataSize] 的 JSON 序列化器，输出 `10MB` 形式的字符串。
 */
class DataSizeSerializer : ValueSerializer<DataSize>() {

    override fun serialize(value: DataSize, gen: JsonGenerator, ctxt: SerializationContext) {
        gen.writeString(formatDataSize(value))
    }
}

/**
 * [DataSize] 的 JSON 反序列化器。
 *
 * 支持两种写法：
 * - 字符串：`"10MB"`、`"1KB"`、`"2048B"`，与序列化输出保持一致
 * - 数字：按字节解释，如 `10485760`（兼容历史配置值）
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

/**
 * 格式化选项，按从大到小的顺序匹配，命中第一个能整除的单位。
 */
private val DATA_SIZE_UNITS = listOf(
    DataUnit.TERABYTES to "TB",
    DataUnit.GIGABYTES to "GB",
    DataUnit.MEGABYTES to "MB",
    DataUnit.KILOBYTES to "KB",
)

/**
 * 将 [DataSize] 格式化为 `10MB` 形式的字符串，如 `2KB`、`10MB`。
 *
 * 优先使用能整除的最大单位，无法整除时退回字节数（如 `1536B`）。
 */
fun formatDataSize(size: DataSize): String {
    val bytes = size.toBytes()
    for ((unit, suffix) in DATA_SIZE_UNITS) {
        val bytesPerUnit = DataSize.of(1, unit).toBytes()
        val amount = bytes / bytesPerUnit
        if (amount != 0L && bytes % bytesPerUnit == 0L) {
            return "$amount$suffix"
        }
    }
    return "${bytes}B"
}
