package com.jiangtj.micro.spring.boot.reactive;

import com.jiangtj.micro.web.FluentWebFilter;
import com.jiangtj.micro.web.FluentWebFilterRegister;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
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

    @Bean
    public FluentWebFilterRegister fluentWebFilterRegister(List<FluentWebFilter> filters) {
        return new FluentWebFilterRegister(filters);
    }

}
