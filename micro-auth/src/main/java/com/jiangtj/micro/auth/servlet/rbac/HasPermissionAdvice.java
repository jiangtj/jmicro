package com.jiangtj.micro.auth.servlet.rbac;

import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.web.aop.AnnotationMethodBeforeAdvice;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class HasPermissionAdvice extends AnnotationMethodBeforeAdvice<HasPermission> implements Ordered {

    @Resource
    private AuthService authService;

    @Override
    public Class<HasPermission> getAnnotationType() {
        return HasPermission.class;
    }

    @Override
    public void before(@NonNull List<HasPermission> annotations, @NonNull Method method, @NonNull Object[] args, Object target) {
        for (HasPermission annotation : annotations) {
            authService.hasPermission(annotation.value());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
