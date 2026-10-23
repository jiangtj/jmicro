package com.jiangtj.micro.test

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Order(Ordered.HIGHEST_PRECEDENCE)
@TestConfiguration(proxyBeanMethods = false)
class JMicroConfiguration {

    @Bean
    fun testAuthContextConverter(): TestAuthContextConverter = TestAuthContextConverter()

    @Bean
    fun defaultTestAuthHandler(): DefaultTestAuthHandler = DefaultTestAuthHandler()
}
