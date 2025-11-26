package com.jiangtj.micro.test;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@JMicroTest
@AutoConfigureMockMvc
@Import({JMicroRestTestClientMvcConfiguration.class})
public @interface JMicroMvcTest {
}
