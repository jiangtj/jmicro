package com.jiangtj.micro.auth.reactive.rbac;

import com.jiangtj.micro.auth.annotations.HasRole;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.reactive.AuthReactorHolder;
import com.jiangtj.micro.auth.reactive.AuthReactorUtils;
import com.jiangtj.micro.auth.reactive.aop.ReactiveAnnotationMethodBeforeAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class HasRoleAdvice extends ReactiveAnnotationMethodBeforeAdvice<HasRole> implements Ordered {

    @Override
    public Class<HasRole> getAnnotationType() {
        return HasRole.class;
    }

    @Override
    public Mono<Void> before(List<HasRole> annotations, Object[] args) {
        Mono<AuthContext> context = AuthReactorHolder.deferAuthContext();
        for (HasRole annotation : annotations) {
            context = context.flatMap(ctx -> AuthReactorUtils.hasRoleHandler(annotation.value()).apply(ctx));
        }
        return context.then();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
