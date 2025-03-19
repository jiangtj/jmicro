package com.jiangtj.micro.spring.boot.reactive;

import com.jiangtj.micro.web.filter.EnableFluentWebFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableFluentWebFilter
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveExceptionAutoConfiguration {

    @Bean
    public BaseExceptionHandler baseExceptionHandler() {
        return new BaseExceptionHandler();
    }

    @Bean
    public NoViewResponseContext noViewResponseContext() {
        return new NoViewResponseContext();
    }

}
