package com.jiangtj.micro.web

import org.springframework.beans.BeanUtils

inline fun <reified T : Any> Any.copyTo(target: T, vararg ignore: String): T {
    BeanUtils.copyProperties(this, target, *ignore)
    return target
}
