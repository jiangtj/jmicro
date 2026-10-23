package com.jiangtj.micro.test;

import com.jiangtj.micro.auth.context.AuthContext;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public interface TestAuthHandler {
    AuthContext convert(Method testMethod, ApplicationContext context);
    AuthContext convert(Class<?> testClass, ApplicationContext context);
}
