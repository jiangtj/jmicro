package com.jiangtj.micro.business.config

import com.jiangtj.micro.common.schema.JsonSchema
import java.lang.reflect.Type
import java.util.function.Function

data class SystemItemInfo(
    var key: String = "",
    var name: String = "",
    var description: String = "",
    var value: String = "",
    var formatedValue: String? = null,
    var secret: Boolean = false,
    var needRefresh: Boolean = false,
    var group: SystemGroup = SystemGroup("default"),
    var type: String = SystemItemType.TEXT.name,
    var provider: List<Pair<String, String>> = listOf(),
    var order: Int = 999,
    var formatter: Function<String, String>? = null,
    var valueFormatter: Function<String, String>? = null,
    var isModified: Boolean = false,
    var tag: List<String> = listOf(),

    /**
     * [SystemItemType.JSON] 类型的目标类型，支持 `Map<String, X>`、`List<X>` 等泛型类型。
     *
     * - 写入时用于校验值能否反序列化为该类型
     * - 读取时可通过 [schema] 拿到前端渲染所需的结构描述
     */
    var jsonType: Type? = null,

    /**
     * [SystemItemType.JSON] 类型下需要脱敏的字段路径，如 `a.b.c` 或 `/a/b/c`。
     *
     * 非空时只脱敏指定路径（[secret] 的整体脱敏不再生效），并且写入时会把值等于
     * `******` 的字段回填为旧值，避免把掩码写回持久化存储。
     */
    var secretPaths: List<String> = listOf(),

    /**
     * 前端渲染用的 JSON Schema，由 [jsonType] 自动生成，无需手动设置。
     */
    var schema: JsonSchema? = null,
)
