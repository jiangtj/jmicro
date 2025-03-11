package com.jiangtj.micro.test;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.web.AnnotationUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Optional;

public class TestAnnotationConverterFactory {

    public static void setAuthContext(ExtensionContext extensionContext, ApplicationContext applicationContext) {
        Class<?> testClass = extensionContext.getRequiredTestClass();
        Method testMethod = extensionContext.getRequiredTestMethod();
        Optional<WithMockAuth> methodAuthHandler = AnnotationUtils.findAnnotation(testMethod, WithMockAuth.class);
        if (methodAuthHandler.isPresent()) {
            TestAuthHandler handler = applicationContext.getBean(methodAuthHandler.get().value());
            AuthContext context = handler.convert(testMethod, applicationContext);
            TestAuthContextHolder.setAuthContext(context);
            return;
        }
        Optional<WithMockAuth> classAuthHandler = AnnotationUtils.findAnnotation(testClass, WithMockAuth.class);
        if (classAuthHandler.isPresent()) {
            TestAuthHandler handler = applicationContext.getBean(classAuthHandler.get().value());
            AuthContext context = handler.convert(testClass, applicationContext);
            TestAuthContextHolder.setAuthContext(context);
            return;
        }
        TestAuthContextHolder.setAuthContext(AuthContext.unLogin());
    }

}
