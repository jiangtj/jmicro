package com.jiangtj.micro.business.config

import java.util.function.Function

data class SystemItemInfo(
    var key: String = "",
    var name: String = "",
    var description: String = "",
    var value: String = "",
    var formatedValue: String? = null,
    var secret: Boolean = false,
    var needRefresh: Boolean = false,
    /** 该配置项的值是否使用 bcrypt 哈希存储（常用于密码类 secret 项）。 */
    var bcrypt: Boolean = false,
    var group: SystemGroup = SystemGroup("default"),
    var type: String = SystemItemType.TEXT.name,
    var provider: List<Pair<String, String>> = listOf(),
    var order: Int = 999,
    var formatter: Function<String, String>? = null,
    var valueFormatter: Function<String, String>? = null,
    var isModified: Boolean = false,
    var tag: List<String> = listOf(),
)
