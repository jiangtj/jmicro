package com.jiangtj.micro.common.exceptions

/**
 * 自定义异常类，继承自 [RuntimeException]，作为项目中自定义异常的基类。
 */
open class MicroHttpException : MicroException {
    val status: Int

    constructor(status: Int) : super("status: $status") {
        this.status = status
    }

    constructor(status: Int, message: String) : super("$message($status)") {
        this.status = status
    }

    constructor(status: Int, message: String, cause: Throwable?) :
            super("$message($status)", cause) {
        this.status = status
    }
}
