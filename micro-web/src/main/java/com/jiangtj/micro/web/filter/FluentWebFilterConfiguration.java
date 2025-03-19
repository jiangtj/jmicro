package com.jiangtj.micro.web.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class FluentWebFilterConfiguration {

    @Bean
    public FluentWebFilterRegister fluentWebFilterRegister(List<FluentWebFilter> filters) {
        return new FluentWebFilterRegister(filters);
    }

}
