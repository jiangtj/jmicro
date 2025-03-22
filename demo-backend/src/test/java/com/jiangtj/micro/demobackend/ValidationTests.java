package com.jiangtj.micro.demobackend;

import com.jiangtj.micro.test.JMicroMvcTest;
import com.jiangtj.micro.test.ProblemDetailConsumer;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

@JMicroMvcTest
public class ValidationTests {

    @Resource
    WebTestClient webClient;

    @Test
    void testValidJavaBean() {
        FoodController.Food food = new FoodController.Food("c", 1);
        webClient.post().uri("/food/create")
            .bodyValue(food)
            .exchange()
            .expectStatus().isOk()
            .expectBody(FoodController.Food.class).isEqualTo(food);
    }

    @Test
    void testInvalidJavaBean() {
        FoodController.Food food = new FoodController.Food("  ", -1);
        webClient.post().uri("/food/create")
            .bodyValue(food)
            .exchange()
            .expectAll(ProblemDetailConsumer.unValidation(
                "name: must not be blank",
                "price: must be greater than or equal to 0"));
    }

    @Test
    void testValidMobilePhone() {
        webClient.post().uri(UriComponentsBuilder.fromUriString("/food/pay1")
                .queryParam("phone", "13800001111")
                .queryParam("name","foodname")
                .build().toUri())
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("13800001111 paid foodname");
        webClient.post().uri(UriComponentsBuilder.fromUriString("/food/pay2")
                .queryParam("phone", 13800001111L)
                .queryParam("name","foodname")
                .build().toUri())
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("13800001111 paid foodname");
    }

    @Test
    void testInvalidMobilePhone() {
        webClient.post().uri(UriComponentsBuilder.fromUriString("/food/pay1")
                .queryParam("phone", "132")
                .queryParam("name","foodname")
                .build().toUri())
            .exchange()
            .expectAll(ProblemDetailConsumer.unValidation("手机号格式不正确"));
        webClient.post().uri(UriComponentsBuilder.fromUriString("/food/pay2")
                .queryParam("phone", 13800)
                .queryParam("name","foodname")
                .build().toUri())
            .exchange()
            .expectAll(ProblemDetailConsumer.unValidation("手机号格式不正确"));
    }

}
