package com.jiangtj.platform.baseservlet;

import com.jiangtj.micro.test.JMicroMvcTest;
import com.jiangtj.micro.test.ProblemDetailConsumer;
import com.jiangtj.micro.test.WithMockUser;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

@JMicroMvcTest
class BaseControllerTests {

    @Resource
    WebTestClient webClient;

    @Test
    void testIndex() {
        webClient.get().uri("/")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("Base Servlet Client Started !!");
    }

    @Test
    void testErr() {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        detail.setDetail("insecure");
        detail.setInstance(URI.create("/insecure/err"));
        webClient.get().uri("/insecure/err")
            .exchange()
            .expectStatus().is4xxClientError()
            .expectBody(ProblemDetail.class).isEqualTo(detail);
    }

    @Test
    @WithMockUser
    void testHaveToken() {
        webClient.get().uri("/needtoken")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testNotHaveToken() {
        webClient.get().uri("/needtoken")
            .exchange()
            .expectStatus().is4xxClientError()
            .expectAll(ProblemDetailConsumer.unLogin().expect());
    }

}
