package com.jiangtj.micro.auth.servlet.rbac;

import com.jiangtj.micro.auth.annotations.HasSubject;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.web.aop.AnnotationMethodBeforeAdvice;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Slf4j
public class HasSubjectAdvice extends AnnotationMethodBeforeAdvice<HasSubject> {

    private final ObjectProvider<AuthService> authService;

    public HasSubjectAdvice(ObjectProvider<AuthService> authService) {
        this.authService = authService;
    }

    @Override
    public Class<HasSubject> getAnnotationType() {
        return HasSubject.class;
    }

    @Override
    public void before(List<HasSubject> annotations, Method method, @Nullable Object[] args, @Nullable Object target) {
        for (HasSubject annotation : annotations) {
            Objects.requireNonNull(authService.getIfAvailable()).hasSubject(Subject.builder()
                .id(annotation.id())
                .name(annotation.name())
                .displayName(annotation.displayName())
                .type(annotation.type())
                .issuer(annotation.issuer())
                .build());
        }
    }
}
