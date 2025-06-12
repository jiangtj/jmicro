package com.jiangtj.micro.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
@TestConfiguration(proxyBeanMethods = false)
public class JMicroConfiguration {

    @Bean
    public TestAuthContextConverter testAuthContextConverter() {
        return new TestAuthContextConverter();
    }

    @Bean
    public DefaultTestAuthHandler defaultTestAuthHandler() {
        return new DefaultTestAuthHandler();
    }

}
