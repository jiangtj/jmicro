package com.jiangtj.micro.test;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WithMockAuth(DefaultTestAuthHandler.class)
public @interface WithMockRole {
    String[] value();
}
