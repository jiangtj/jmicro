package com.jiangtj.micro.demoreactive;

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
class BaseControllerTests {
    
    @Resource
    WebTestClient client;

    @Test
    void testIndex() {
        client.get().uri("/")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("Base Reactive Client Started !!");
    }

    @Test
    void testErr() {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        detail.setDetail("insecure");
        detail.setInstance(URI.create("/insecure/err"));
        client.get().uri("/insecure/err")
            .exchange()
            .expectStatus().is4xxClientError()
            .expectBody(ProblemDetail.class).isEqualTo(detail);
    }

    @Test
    @WithMockUser
    void testHaveToken() {
        client.get().uri("/needtoken")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testNotHaveToken() {
        client.get().uri("/needtoken")
            .exchange()
            .expectAll(ProblemDetailConsumer.unLogin().expect());
    }

    @Test
    @WithMockUser(roles = "role-test1")
    void testHaveRoleAnnotations() {
        client.get().uri("/role-test-1")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = "role-test1")
    void testNotHaveRoleAnnotations() {
        client.get().uri("/role-test-2")
            .exchange()
            .expectAll(ProblemDetailConsumer.unRole("role-test2").expect());
    }

}
