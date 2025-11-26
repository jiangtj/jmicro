package com.jiangtj.micro.spring.boot.servlet;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.json.JsonMapper;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletExceptionAutoConfiguration {

    @Bean
    public JsonResponseContext noViewResponseContext(JsonMapper jsonMapper) {
        return new JsonResponseContext(jsonMapper);
    }

    @Bean
    public ServletExceptionHandler servletExceptionHandler() {
        return new ServletExceptionHandler();
    }

    @Bean
    public BaseExceptionFilter baseExceptionFilter() {
        return new BaseExceptionFilter();
    }

    @Bean
    public BaseExceptionResolver baseExceptionResolver() {
        return new BaseExceptionResolver();
    }

    @Bean
    public ServletProblemDetailsExceptionHandler servletProblemDetailsExceptionHandler() {
        return new ServletProblemDetailsExceptionHandler();
    }

    /*@Bean
    public MethodValidationPostProcessor validationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setAdaptConstraintViolations(true);
        return processor;
    }*/

    /*@Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }*/

}
