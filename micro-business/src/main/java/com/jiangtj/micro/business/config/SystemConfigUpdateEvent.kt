package com.jiangtj.micro.business.config

data class SystemConfigUpdateEvent(
    val key: String,
    val oldV: String,
    val newV: String
)
