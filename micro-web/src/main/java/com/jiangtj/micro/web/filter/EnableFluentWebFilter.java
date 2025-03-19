package com.jiangtj.micro.web.filter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(FluentWebFilterConfiguration.class)
public @interface EnableFluentWebFilter {
}
