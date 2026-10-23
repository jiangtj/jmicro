package com.jiangtj.micro.test

import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@WithMockAuth(DefaultTestAuthHandler::class)
annotation class WithMockRole(val value: Array<String>)
