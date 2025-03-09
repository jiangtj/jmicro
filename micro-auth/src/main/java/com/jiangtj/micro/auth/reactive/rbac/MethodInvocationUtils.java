package com.jiangtj.micro.auth.reactive.rbac;

import com.jiangtj.micro.auth.context.AuthContext;
import org.aopalliance.intercept.MethodInvocation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MethodInvocationUtils {
    static Object handleAdvice(Mono<AuthContext> ctx, MethodInvocation invocation) throws Throwable {
        Object proceed = invocation.proceed();
        if (proceed instanceof Mono<?> mono) {
            return ctx.then(mono);
        }
        if (proceed instanceof Flux<?> flux) {
            return ctx.thenMany(flux);
        }
        return proceed;
    }
}
