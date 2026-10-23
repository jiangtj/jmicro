package com.jiangtj.micro.demobackend

import com.jiangtj.micro.auth.annotations.HasRole
import com.jiangtj.micro.auth.annotations.Logic

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@HasRole(value = ["admin", "user"], logic = Logic.OR)
annotation class HasAdminOrUser
