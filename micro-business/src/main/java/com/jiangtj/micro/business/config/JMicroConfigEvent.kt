package com.jiangtj.micro.business.config

class JMicroConfigRefreshEvent

data class JMicroConfigUpdateEvent(
    val key: String,
    val oldV: String,
    val newV: String
)