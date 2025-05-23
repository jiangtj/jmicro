package com.jiangtj.micro.auth.servlet.rbac;

import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.web.aop.AnnotationMethodBeforeAdvice;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Slf4j
public class HasPermissionAdvice extends AnnotationMethodBeforeAdvice<HasPermission> {

    private final ObjectProvider<AuthService> authService;

    public HasPermissionAdvice(ObjectProvider<AuthService> authService) {
        this.authService = authService;
    }

    @Override
    public Class<HasPermission> getAnnotationType() {
        return HasPermission.class;
    }

    @Override
    public void before(@NonNull List<HasPermission> annotations, @NonNull Method method, @NonNull Object[] args, @Nullable Object target) {
        for (HasPermission annotation : annotations) {
            Objects.requireNonNull(authService.getIfAvailable()).hasPermission(annotation.logic(), annotation.value());
        }
    }
}
