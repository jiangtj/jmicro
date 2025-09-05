package com.jiangtj.micro.common.utils

import kotlin.reflect.full.memberProperties

fun <T : Any> T.toMap(): Map<String, Any?> {
    return this::class.memberProperties.associate { property ->
        property.name to property.getter.call(this)
    }
}
