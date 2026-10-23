package com.jiangtj.micro.test

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import java.net.URI

interface ProblemDetails {

    companion object {

        @JvmStatic
        fun unLogin(uri: String): ProblemDetail {
            val detail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)
            detail.title = "未登录"
            detail.detail = "缺少认证信息，请在header中携带token"
            detail.instance = URI.create(uri)
            return detail
        }
    }
}
