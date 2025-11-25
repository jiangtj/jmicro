package com.jiangtj.micro.demobackend;

import com.jiangtj.micro.test.JMicroMvcTest;
import com.jiangtj.micro.test.ProblemDetailMvcConsumer;
import com.jiangtj.micro.test.WithMockUser;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.client.RestTestClient;

@JMicroMvcTest
class RBACControllerTest {

    @Resource
    RestTestClient client;

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
                .expectAll(ProblemDetailMvcConsumer.unLogin().expect());
    }
}