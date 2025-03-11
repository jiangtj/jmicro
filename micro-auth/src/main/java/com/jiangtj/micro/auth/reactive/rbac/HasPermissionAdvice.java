package com.jiangtj.micro.auth.reactive.rbac;

import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.reactive.AuthReactorHolder;
import com.jiangtj.micro.auth.reactive.AuthReactorService;
import com.jiangtj.micro.auth.reactive.aop.ReactiveAnnotationMethodBeforeAdvice;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class HasPermissionAdvice extends ReactiveAnnotationMethodBeforeAdvice<HasPermission> implements Ordered {

    @Resource
    private AuthReactorHolder authReactorHolder;
    @Resource
    private AuthReactorService authReactorService;

    @Override
    public Class<HasPermission> getAnnotationType() {
        return HasPermission.class;
    }

    @Override
    public Mono<Void> before(List<HasPermission> annotations, Object[] args) {
        Mono<AuthContext> context = authReactorHolder.deferAuthContext();
        for (HasPermission annotation : annotations) {
            context = context.flatMap(ctx -> authReactorService.hasPermissionHandler(annotation.value()).apply(ctx));
        }
        return context.then();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
