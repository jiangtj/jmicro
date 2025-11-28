package com.jiangtj.micro.auth.servlet.rbac;

import com.jiangtj.micro.auth.core.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
public class HasLoginAdvice implements MethodBeforeAdvice {

    private final ObjectProvider<AuthService> authService;

    public HasLoginAdvice(ObjectProvider<AuthService> authService) {
        this.authService = authService;
    }

    @Override
    public void before(@NonNull Method method, @NonNull Object[] args, @Nullable Object target) throws Throwable {
        Objects.requireNonNull(authService.getIfAvailable()).hasLogin();
    }
}
