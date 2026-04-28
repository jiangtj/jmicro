package com.jiangtj.micro.common.utils

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun Any.trimAllStringProperties() {
    this::class.memberProperties
        .filter { it.returnType.classifier == String::class }
        .filterIsInstance<KMutableProperty<*>>()
        .forEach { property ->
            property.isAccessible = true
            val currentValue = property.getter.call(this) as? String
            if (currentValue != null) {
                val trim = currentValue.trim()
                property.setter.call(this, trim)
                if (property.returnType.isMarkedNullable && trim.isEmpty()){
                    property.setter.call(this, null)
                }
            }
        }
}
