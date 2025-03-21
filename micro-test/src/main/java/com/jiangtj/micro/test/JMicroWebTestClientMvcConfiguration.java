package com.jiangtj.micro.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;


@TestConfiguration(proxyBeanMethods = false)
public class JMicroWebTestClientMvcConfiguration {

    @Bean
    public WebTestClient webTestClient(MockMvc mvc) {
        return MockMvcWebTestClient.bindTo(mvc).build();
    }

}
