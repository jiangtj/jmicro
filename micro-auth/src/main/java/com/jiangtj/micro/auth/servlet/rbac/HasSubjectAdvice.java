package com.jiangtj.micro.auth.servlet.rbac;

import com.jiangtj.micro.auth.annotations.HasSubject;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.web.aop.AnnotationMethodBeforeAdvice;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class HasSubjectAdvice extends AnnotationMethodBeforeAdvice<HasSubject> implements Ordered {

    @Resource
    private AuthService authService;

    @Override
    public Class<HasSubject> getAnnotationType() {
        return HasSubject.class;
    }

    @Override
    public void before(List<HasSubject> annotations, Method method, Object[] args, @Nullable Object target) {
        for (HasSubject annotation : annotations) {
            authService.hasSubject(Subject.builder()
                .id(annotation.id())
                .name(annotation.name())
                .displayName(annotation.displayName())
                .type(annotation.type())
                .issuer(annotation.issuer())
                .build());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
