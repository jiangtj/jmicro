package com.jiangtj.micro.demobackend

import com.jiangtj.micro.test.JMicroMvcTest
import com.jiangtj.micro.test.ProblemDetailMvcConsumer
import com.jiangtj.micro.test.WithMockUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.client.RestTestClient

@JMicroMvcTest
class RBACControllerTest {

    @Autowired
    lateinit var client: RestTestClient

    @Test
    @WithMockUser
    fun hasLoginWithToken() {
        client.get().uri("/anno/hasLogin")
            .exchange()
            .expectStatus().isOk()
    }

    @Test
    fun hasLoginWithoutToken() {
        client.get().uri("/anno/hasLogin")
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.unLogin().expect())
    }
}
