package com.jiangtj.micro.business.config

class SystemConfigRefreshEvent

data class SystemConfigUpdateEvent(
    val key: String,
    val oldV: String,
    val newV: String
)