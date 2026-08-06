package com.jiangtj.micro.business.config

import java.util.function.Function

data class SystemItem(
    var key: String,
    var name: String = "",
    var description: String = "-",
    var value: String = "",
    var secret: Boolean = false,
    var needRefresh: Boolean = false,
    var group: SystemGroup = SystemGroup("default"),
    var type: SystemItemType = SystemItemType.TEXT,
    var fileDefaultPath: String = "",
    var provider: List<Pair<String, String>> = listOf(),
    var order: Int = 999,
    var formatter: Function<String, String>? = null,
    var valueFormatter: Function<String, String>? = null,
    var tag: List<String> = listOf()
)
