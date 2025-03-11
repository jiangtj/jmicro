package com.jiangtj.platform.basereactive;

import com.jiangtj.micro.test.JMicroTest;
import com.jiangtj.micro.test.ProblemDetailConsumer;
import com.jiangtj.micro.test.WithMockUser;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

@JMicroTest
@AutoConfigureWebTestClient
class BaseRouterTests {

    @Resource
    WebTestClient client;

    @Test
    void testErr() {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        detail.setDetail("fn");
        detail.setInstance(URI.create("/insecure/fn/err"));
        client.get().uri("/insecure/fn/err")
            .exchange()
            .expectStatus().is4xxClientError()
            .expectBody(ProblemDetail.class).isEqualTo(detail);
    }

    @Test
    @WithMockUser
    void testHaveToken() {
        client.get().uri("/fn/needtoken")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testNotHaveToken() {
        client.get().uri("/fn/needtoken")
            .exchange()
            .expectAll(ProblemDetailConsumer.unLogin().expect());
    }

}
