package com.jiangtj.micro.business.config

enum class SystemItemType {
    TEXT, TEXTAREA, SWITCH, SELECT, MULTI_SELECT,

    /**
     * 结构化配置，值为 JSON 文本，用于表达对象、集合以及 `Map` 等嵌套结构。
     *
     * 需配合 [SystemItemInfo.jsonType] 声明目标类型，用于写入校验与 schema 生成。
     */
    JSON,
}
