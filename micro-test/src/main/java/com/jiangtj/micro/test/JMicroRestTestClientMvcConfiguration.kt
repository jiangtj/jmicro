package com.jiangtj.micro.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;


@TestConfiguration(proxyBeanMethods = false)
public class JMicroRestTestClientMvcConfiguration {

    @Bean
    public RestTestClient webTestClient(MockMvc mvc) {
        return RestTestClient.bindTo(mvc).build();
    }

}
