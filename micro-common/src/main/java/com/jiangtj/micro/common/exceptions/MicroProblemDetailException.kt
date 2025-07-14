package com.jiangtj.micro.common.exceptions

/**
 * 自定义异常类，继承自 [MicroHttpException]，用于表示包含问题详情的 HTTP 异常。
 */
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
