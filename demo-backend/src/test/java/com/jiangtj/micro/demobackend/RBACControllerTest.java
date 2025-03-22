package com.jiangtj.micro.demobackend;

import com.jiangtj.micro.test.JMicroMvcTest;
import com.jiangtj.micro.test.ProblemDetailConsumer;
import com.jiangtj.micro.test.WithMockUser;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

@JMicroMvcTest
class RBACControllerTest {

    @Resource
    WebTestClient client;

    @Test
    @WithMockUser
    void hasLoginWithToken() {
        client.get().uri("/anno/hasLogin")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void hasLoginWithoutToken() {
        client.get().uri("/anno/hasLogin")
                .exchange()
                .expectAll(ProblemDetailConsumer.unLogin().expect());
    }
}