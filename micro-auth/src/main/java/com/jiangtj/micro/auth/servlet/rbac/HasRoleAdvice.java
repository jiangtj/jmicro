package com.jiangtj.micro.auth.servlet.rbac;

import com.jiangtj.micro.auth.annotations.HasRole;
import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.web.aop.AnnotationMethodBeforeAdvice;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Slf4j
public class HasRoleAdvice extends AnnotationMethodBeforeAdvice<HasRole> {

    private final ObjectProvider<AuthService> authService;

    public HasRoleAdvice(ObjectProvider<AuthService> authService) {
        this.authService = authService;
    }

    @Override
    public Class<HasRole> getAnnotationType() {
        return HasRole.class;
    }

    @Override
    public void before(List<HasRole> annotations, Method method, @Nullable Object[] args, @Nullable Object target) {
        for (HasRole annotation : annotations) {
            Objects.requireNonNull(authService.getIfAvailable()).hasRole(annotation.logic(), annotation.value());
        }
    }
}
