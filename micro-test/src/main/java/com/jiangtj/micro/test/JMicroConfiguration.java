package com.jiangtj.micro.test;

import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@Order(Ordered.HIGHEST_PRECEDENCE)
@TestConfiguration(proxyBeanMethods = false)
public class JMicroConfiguration {

    @Bean
    public TestAuthContextConverter testAuthContextConverter() {
        return new TestAuthContextConverter();
    }

    @Bean
    public SimpleTestAuthHandler simpleTestAuthContextConverter() {
        return new SimpleTestAuthHandler();
    }

}
