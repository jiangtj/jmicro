package com.jiangtj.micro.web

import com.jiangtj.micro.common.exceptions.MicroHttpException
import com.jiangtj.micro.common.exceptions.MicroProblemDetailException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail

fun RuntimeException.toProblemDetail(): ProblemDetail {
    if (this is MicroProblemDetailException) {
        val problemDetail = ProblemDetail.forStatus(this.status)
        problemDetail.title = this.title
        problemDetail.detail = this.message ?: "服务端异常"
        return problemDetail
    }
    if (this is MicroHttpException) {
        val problemDetail = ProblemDetail.forStatus(this.status)
        problemDetail.detail = this.message ?: "服务端异常"
        return problemDetail
    }
    return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, this.message ?: "服务端异常")
}