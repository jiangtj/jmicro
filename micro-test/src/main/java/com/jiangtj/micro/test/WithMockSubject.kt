package com.jiangtj.micro.test

import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@WithMockAuth(DefaultTestAuthHandler::class)
annotation class WithMockSubject(
    val id: String,
    val name: String = "",
    val displayName: String = "",
    val type: String = "",
    val issuer: String = "",
)
