package com.jiangtj.micro.auth.reactive.rbac;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.reactive.AuthReactorHolder;
import com.jiangtj.micro.auth.reactive.AuthReactorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

@Slf4j
public class HasLoginAdvice implements MethodInterceptor, Ordered {

    @Resource
    private AuthReactorHolder authReactorHolder;
    @Resource
    private AuthReactorService authReactorService;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Mono<AuthContext> context = authReactorHolder.deferAuthContext()
                .flatMap(ctx -> authReactorService.hasLoginHandler().apply(ctx));

        return MethodInvocationUtils.handleAdvice(context, invocation);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
