package com.jiangtj.micro.test

import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension

class JMicroExtension : BeforeTestExecutionCallback, AfterTestExecutionCallback {

    override fun beforeTestExecution(extensionContext: ExtensionContext) {
        val applicationContext = SpringExtension.getApplicationContext(extensionContext)
        TestAnnotationConverterFactory.setAuthContext(extensionContext, applicationContext)
    }

    override fun afterTestExecution(extensionContext: ExtensionContext) {
        TestAuthContextHolder.clearAuthContext()
    }
}
