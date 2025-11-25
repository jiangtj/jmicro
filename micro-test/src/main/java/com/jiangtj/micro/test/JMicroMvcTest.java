package com.jiangtj.micro.test;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@JMicroTest
@Import({JMicroWebTestClientMvcConfiguration.class})
public @interface JMicroMvcTest {
}
