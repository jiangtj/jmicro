package com.jiangtj.micro.test

import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class WithMockAuth(val value: KClass<out TestAuthHandler>)
