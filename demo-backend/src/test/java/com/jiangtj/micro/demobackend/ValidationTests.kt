package com.jiangtj.micro.demobackend

import com.jiangtj.micro.demobackend.controller.FoodController
import com.jiangtj.micro.test.JMicroMvcTest
import com.jiangtj.micro.test.ProblemDetailMvcConsumer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.web.util.UriComponentsBuilder

@JMicroMvcTest
class ValidationTests {

    @Autowired
    lateinit var webClient: RestTestClient

    @Test
    fun testValidJavaBean() {
        val food = FoodController.Food("c", 1)
        webClient.post().uri("/food/create")
            .body(food)
            .exchange()
            .expectStatus().isOk()
            .expectBody(FoodController.Food::class.java).isEqualTo(food)
    }

    @Test
    fun testInvalidJavaBean() {
        val food = FoodController.Food("  ", -1)
        webClient.post().uri("/food/create")
            .body(food)
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.badRequest("Invalid request content."))
    }

    @Test
    fun testValidMobilePhone() {
        webClient.post().uri(
            UriComponentsBuilder.fromUriString("/food/pay1")
                .queryParam("phone", "13800001111")
                .queryParam("name", "foodname")
                .build().toUri()
        )
            .exchange()
            .expectStatus().isOk()
            .expectBody(String::class.java)
            .isEqualTo("13800001111 paid foodname")
        webClient.post().uri(
            UriComponentsBuilder.fromUriString("/food/pay2")
                .queryParam("phone", 13800001111L)
                .queryParam("name", "foodname")
                .build().toUri()
        )
            .exchange()
            .expectStatus().isOk()
            .expectBody(String::class.java)
            .isEqualTo("13800001111 paid foodname")
    }

    @Test
    fun testInvalidMobilePhone() {
        webClient.post().uri(
            UriComponentsBuilder.fromUriString("/food/pay1")
                .queryParam("phone", "132")
                .queryParam("name", "foodname")
                .build().toUri()
        )
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.badRequest("Validation failure"))
        webClient.post().uri(
            UriComponentsBuilder.fromUriString("/food/pay2")
                .queryParam("phone", 13800)
                .queryParam("name", "foodname")
                .build().toUri()
        )
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.badRequest("Validation failure"))
    }
}
