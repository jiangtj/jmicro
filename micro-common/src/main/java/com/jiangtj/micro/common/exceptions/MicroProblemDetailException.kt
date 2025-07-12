package com.jiangtj.micro.common.exceptions

open class MicroProblemDetailException : MicroHttpException {
    val title: String
    val detail: String

    constructor(status: Int, title: String, detail: String) : super(status, "$title: $detail") {
        this.title = title
        this.detail = detail
    }

    constructor(status: Int, title: String, detail: String, cause: Throwable?) :
            super(status, "$title: $detail", cause) {
        this.title = title
        this.detail = detail
    }
}
