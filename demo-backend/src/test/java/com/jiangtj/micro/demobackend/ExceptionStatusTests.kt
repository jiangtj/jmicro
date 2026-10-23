package com.jiangtj.micro.demobackend

import com.jiangtj.micro.test.JMicroMvcTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.test.web.servlet.client.RestTestClient
import java.net.URI

@JMicroMvcTest
class ExceptionStatusTests {

    @Autowired
    lateinit var webClient: RestTestClient

    @Test
    fun testErr2() {
        val detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        detail.title = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase
        detail.detail = "系统错误"
        detail.instance = URI.create("/insecure/err2")
        webClient.get().uri("/insecure/err2")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody(ProblemDetail::class.java).isEqualTo(detail)
    }

    @Test
    fun testFnErr2() {
        val detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        detail.title = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase
        detail.detail = "系统错误"
        detail.instance = URI.create("/insecure/fn/err2")
        webClient.get().uri("/insecure/fn/err2")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody(ProblemDetail::class.java).isEqualTo(detail)
    }
}
