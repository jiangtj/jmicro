package com.jiangtj.micro.common.utils

object ListUtils {

    @JvmStatic
    fun isList(type: Class<*>): Boolean {
        return type.isArray || MutableCollection::class.java.isAssignableFrom(type)
    }

}