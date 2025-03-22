package com.jiangtj.micro.auth.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasSubject {
    @AliasFor("id")
    String value() default "";
    @AliasFor("value")
    String id() default "";
    String name() default "";
    String displayName() default "";
    String type() default "";
    String issuer() default "";
}
