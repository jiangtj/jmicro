package com.jiangtj.micro.test;

import org.junit.jupiter.api.extension.Extension;

import java.lang.annotation.*;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WithMockAuth {
    Class<? extends TestAuthHandler> value();
}
