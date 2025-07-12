package com.jiangtj.micro.common.exceptions

open class MicroException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable?) : super(message, cause)
}
