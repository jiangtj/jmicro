package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import jakarta.annotation.Resource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AuthReactiveHolder {

    @Resource
    private AuthContextFactory factory;

    public Mono<ServerWebExchange> deferExchange() {
        return Mono.deferContextual(ctx -> Mono.just(ctx.get(ServerWebExchange.class)));
    }

    public Mono<AuthContext> deferAuthContext() {
        return Mono.deferContextual(ctx -> Mono.just(ctx.get(AuthContext.class)));
    }

}
