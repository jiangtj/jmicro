package com.jiangtj.micro.test

import com.jiangtj.micro.auth.context.AuthContext
import com.jiangtj.micro.web.AnnotationUtils
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.context.ApplicationContext

object TestAnnotationConverterFactory {

    @JvmStatic
    fun setAuthContext(extensionContext: ExtensionContext, applicationContext: ApplicationContext) {
        val testClass = extensionContext.requiredTestClass
        val testMethod = extensionContext.requiredTestMethod
        val authHandler = AnnotationUtils.findAnnotation(testMethod, WithMockAuth::class.java)
            .or { AnnotationUtils.findAnnotation(testClass, WithMockAuth::class.java) }
        if (authHandler.isPresent) {
            val handler = applicationContext.getBean(authHandler.get().value.java)
            TestAuthContextHolder.setAuthContext(handler.convert(testMethod, applicationContext))
            return
        }
        TestAuthContextHolder.setAuthContext(AuthContext.unLogin())
    }
}
