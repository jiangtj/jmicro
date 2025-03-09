package com.jiangtj.micro.auth.reactive.rbac;

import com.jiangtj.micro.auth.annotations.TokenType;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.reactive.AuthReactorHolder;
import com.jiangtj.micro.auth.reactive.AuthReactorUtils;
import com.jiangtj.micro.auth.reactive.aop.ReactiveAnnotationMethodBeforeAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class HasTokenTypeAdvice extends ReactiveAnnotationMethodBeforeAdvice<TokenType> implements Ordered {

    @Override
    public Class<TokenType> getAnnotationType() {
        return TokenType.class;
    }

    @Override
    public Mono<Void> before(List<TokenType> annotations, Object[] args) {
        Mono<AuthContext> context = AuthReactorHolder.deferAuthContext();
        for (TokenType annotation : annotations) {
            context = context.flatMap(ctx -> AuthReactorUtils.tokenTypeHandler(annotation.value()).apply(ctx));
        }
        return context.then();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
