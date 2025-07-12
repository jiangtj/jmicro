package com.jiangtj.micro.common.exceptions

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
