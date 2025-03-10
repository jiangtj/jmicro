package com.jiangtj.micro.auth.annotations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

@Slf4j
@Deprecated
public class RBACMethodPointcut extends StaticMethodMatcherPointcut {
    @Override
    public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
        return RBACAnnotationUtils.hasAnnotations(method, targetClass);
    }
}
