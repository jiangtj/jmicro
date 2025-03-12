package com.jiangtj.micro.auth.reactive.rbac;

import com.jiangtj.micro.auth.core.AuthReactiveService;
import com.jiangtj.micro.auth.reactive.aop.ReactiveMethodBeforeAdvice;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

@Slf4j
public class HasLoginAdvice extends ReactiveMethodBeforeAdvice implements Ordered {

    @Resource
    private AuthReactiveService authReactiveService;

    @Override
    public Mono<Void> before(@NonNull Method method, @NonNull Object[] args, @Nullable Object target) throws Throwable {
        return authReactiveService.hasLogin();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
