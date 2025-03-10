package com.jiangtj.micro.test;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WithMockAuth(SimpleTestAuthHandler.class)
public @interface WithMockSubject {
    String id();
    String name() default "";
    String displayName() default "";
}
