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
        Optional<WithMockAuth> authHandler = AnnotationUtils.findAnnotation(testMethod, WithMockAuth.class)
            .or(() -> AnnotationUtils.findAnnotation(testClass, WithMockAuth.class));
        if (authHandler.isPresent()) {
            TestAuthHandler handler = applicationContext.getBean(authHandler.get().value());
            AuthContext context = handler.convert(testMethod, applicationContext);
            TestAuthContextHolder.setAuthContext(context);
            return;
        }
        TestAuthContextHolder.setAuthContext(AuthContext.unLogin());
    }

}
