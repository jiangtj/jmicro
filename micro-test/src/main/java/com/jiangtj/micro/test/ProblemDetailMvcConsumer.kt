package com.jiangtj.micro.test

import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.test.web.servlet.client.RestTestClient

class ProblemDetailMvcConsumer internal constructor(code: HttpStatusCode) {

    private val problemDetail: ProblemDetail = ProblemDetail.forStatus(code)

    fun title(title: String): ProblemDetailMvcConsumer = apply { problemDetail.title = title }

    fun detail(detail: String): ProblemDetailMvcConsumer = apply { problemDetail.detail = detail }

    fun expect(): RestTestClient.ResponseSpec.ResponseSpecConsumer =
        RestTestClient.ResponseSpec.ResponseSpecConsumer { responseSpec ->
            responseSpec.expectStatus().is4xxClientError()
            responseSpec.expectStatus().isEqualTo(problemDetail.status)
            val expectBody = responseSpec.expectBody()
            expectBody
                .jsonPath("status").isEqualTo(problemDetail.status)
                .jsonPath("instance").exists()
            problemDetail.title?.let { expectBody.jsonPath("title").isEqualTo(it) }
            problemDetail.detail?.let { expectBody.jsonPath("detail").isEqualTo(it) }
        }

    companion object {

        @JvmStatic
        fun unLogin(): ProblemDetailMvcConsumer =
            ProblemDetailMvcConsumer(HttpStatus.UNAUTHORIZED)
                .title("No Login")
                .detail("You have to take a token for this request.")

        // {"type":"about:blank","title":"Invalid Token","status":403,"detail":"不支持的
        // Auth Context","instance":"/actuator"}
        @JvmStatic
        fun unInvalid(msg: String): ProblemDetailMvcConsumer =
            ProblemDetailMvcConsumer(HttpStatus.FORBIDDEN)
                .title("Invalid Token")
                .detail(msg)

        // {"type":"about:blank","title":"No Role","status":403,"detail":"Don't have
        // role<roletest2>.","instance":"/role-test-2"}
        @JvmStatic
        fun unRole(role: String): ProblemDetailMvcConsumer =
            ProblemDetailMvcConsumer(HttpStatus.FORBIDDEN)
                .title("No Role")
                .detail("Don't have role<$role>.")

        // {"type":"about:blank","title":"No Permission","status":403,"detail":"Don't
        // have permission<system:user:write>","instance":"/user"}
        @JvmStatic
        fun unPermission(permissionName: String): ProblemDetailMvcConsumer =
            ProblemDetailMvcConsumer(HttpStatus.FORBIDDEN)
                .title("No Permission")
                .detail("Don't have permission<$permissionName>.")

        @JvmStatic
        fun forStatus(status: HttpStatus): ProblemDetailMvcConsumer =
            ProblemDetailMvcConsumer(status).title(status.reasonPhrase)

        // {"type":"about:blank","title":"Validation
        // failure","status":400,"detail":"手机号格式不正确"}
        @JvmStatic
        fun unValidation(vararg failures: String): RestTestClient.ResponseSpec.ResponseSpecConsumer =
            RestTestClient.ResponseSpec.ResponseSpecConsumer { responseSpec ->
                responseSpec.expectStatus().is4xxClientError()
                responseSpec.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                responseSpec.expectBody()
                    .jsonPath("status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                    .jsonPath("title").isEqualTo("Validation failure")
                    .jsonPath("detail").value(String::class.java) { detail ->
                        failures.forEach { assertTrue(detail.contains(it)) }
                    }
                    .jsonPath("instance").exists()
            }

        // {"detail":"Invalid request content.","instance":"/food/create","status":400,"title":"Bad Request"}
        @JvmStatic
        fun badRequest(failure: String): RestTestClient.ResponseSpec.ResponseSpecConsumer =
            RestTestClient.ResponseSpec.ResponseSpecConsumer { responseSpec ->
                responseSpec.expectStatus().is4xxClientError()
                responseSpec.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                responseSpec.expectBody()
                    .jsonPath("status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                    .jsonPath("title").isEqualTo("Bad Request")
                    .jsonPath("detail").value(String::class.java) { detail ->
                        assertTrue(detail.contains(failure))
                    }
                    .jsonPath("instance").exists()
            }
    }
}
