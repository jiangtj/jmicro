package com.jiangtj.micro.demobackend

import com.jiangtj.micro.test.WithMockUser
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@WithMockUser(subject = "admin", roles = ["admin"])
annotation class WithMockAdmin
