package com.jiangtj.micro.test;

import org.junit.jupiter.api.extension.*;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class JMicroExtension implements BeforeEachCallback,
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);
        TestAnnotationConverterFactory.setAuthContext(extensionContext, applicationContext);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        TestAuthContextHolder.clearAuthContext();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
    }
}
