package com.jiangtj.micro.demobackend

import com.jiangtj.micro.test.JMicroMvcTest
import com.jiangtj.micro.test.ProblemDetailMvcConsumer
import com.jiangtj.micro.test.WithMockUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.test.web.servlet.client.RestTestClient
import java.net.URI

@JMicroMvcTest
class BaseRouterTests {

    @Autowired
    lateinit var webClient: RestTestClient

    @Test
    fun testErr() {
        val detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        detail.title = HttpStatus.BAD_REQUEST.reasonPhrase
        detail.detail = "fn"
        detail.instance = URI.create("/insecure/fn/err")
        webClient.get().uri("/insecure/fn/err")
            .exchange()
            .expectStatus().is4xxClientError()
            .expectBody(ProblemDetail::class.java).isEqualTo(detail)
    }

    @Test
    @WithMockUser
    fun testHaveToken() {
        webClient.get().uri("/fn/needtoken")
            .exchange()
            .expectStatus().isOk()
    }

    @Test
    fun testNotHaveToken() {
        webClient.get().uri("/fn/needtoken")
            .exchange()
            .expectStatus().is4xxClientError()
            .expectAll(ProblemDetailMvcConsumer.unLogin().expect())
    }
}
