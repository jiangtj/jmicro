package com.jiangtj.micro.web.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public abstract class ReactiveMethodBeforeAdvice implements MethodInterceptor {

    abstract public Mono<Void> before(Method method, @Nullable Object[] args, @Nullable Object target)
            throws Throwable;

    @Nullable
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Mono<Void> beforeProceed = before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        Object proceed = invocation.proceed();

        if (proceed instanceof Mono<?> mono) {
            return beforeProceed.then(mono);
        }

        if (proceed instanceof Flux<?> flux) {
            return beforeProceed.thenMany(flux);
        }

        // 不拦截非响应式的接口
        return proceed;
    }

}
