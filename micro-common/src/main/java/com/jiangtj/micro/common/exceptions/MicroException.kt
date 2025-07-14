package com.jiangtj.micro.common.exceptions

/**
 * 自定义异常类，继承自 [RuntimeException]，作为项目中自定义异常的基类。
 */
open class MicroException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable?) : super(message, cause)
}
