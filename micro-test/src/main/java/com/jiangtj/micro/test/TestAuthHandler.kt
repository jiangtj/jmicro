package com.jiangtj.micro.test

import com.jiangtj.micro.auth.context.AuthContext
import org.springframework.context.ApplicationContext
import java.lang.reflect.Method

interface TestAuthHandler {

    fun convert(testMethod: Method, context: ApplicationContext): AuthContext

    fun convert(testClass: Class<*>, context: ApplicationContext): AuthContext
}
