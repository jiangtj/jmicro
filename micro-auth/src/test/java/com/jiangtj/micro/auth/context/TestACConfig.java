package com.jiangtj.micro.auth.context;

import org.springframework.context.annotation.Bean;

public class TestACConfig {
    @Bean
    public Test1C test1C() {
        return new Test1C();
    }

    @Bean
    public Test2C test2C() {
        return new Test2C();
    }

    @Bean
    public Test3C test3C() {
        return new Test3C();
    }
    @Bean
    public Test1H test1H() {
        return new Test1H();
    }

    @Bean
    public Test2H test2H() {
        return new Test2H();
    }

    @Bean
    public Test3H test3H() {
        return new Test3H();
    }
}
