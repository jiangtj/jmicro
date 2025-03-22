package com.jiangtj.micro.auth.reactive.rbac;

import com.jiangtj.micro.auth.annotations.HasSubject;
import com.jiangtj.micro.auth.context.Subject;
import com.jiangtj.micro.auth.core.AuthReactiveService;
import com.jiangtj.micro.web.aop.ReactiveAnnotationMethodBeforeAdvice;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class HasSubjectAdvice extends ReactiveAnnotationMethodBeforeAdvice<HasSubject> implements Ordered {

    @Resource
    private AuthReactiveService authReactiveService;

    @Override
    public Class<HasSubject> getAnnotationType() {
        return HasSubject.class;
    }

    @Override
    public Mono<Void> before(List<HasSubject> annotations, Method method, Object[] args, @Nullable Object target) {
        return Flux.fromIterable(annotations)
            .flatMap(annotation -> authReactiveService.hasSubject(Subject.builder()
                .id(annotation.id())
                .name(annotation.name())
                .displayName(annotation.displayName())
                .type(annotation.type())
                .issuer(annotation.issuer())
                .build()))
            .then();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
