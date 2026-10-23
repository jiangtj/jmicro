package com.jiangtj.micro.test

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@JMicroTest
@AutoConfigureMockMvc
@Import(JMicroRestTestClientMvcConfiguration::class)
annotation class JMicroMvcTest
