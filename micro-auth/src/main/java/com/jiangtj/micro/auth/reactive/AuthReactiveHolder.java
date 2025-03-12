package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import jakarta.annotation.Resource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class AuthReactiveHolder {

    @Resource
    private AuthContextFactory factory;

    public Mono<ServerWebExchange> deferExchange() {
        return Mono.deferContextual(ctx -> Mono.just(ctx.get(ServerWebExchange.class)));
    }

    public Mono<AuthContext> deferAuthContext() {
        return Mono.deferContextual(ctx -> {
            Optional<AuthContext> context = ctx.getOrEmpty(AuthContext.class);
            return Mono.justOrEmpty(context)
                .switchIfEmpty(deferExchange()
                    .map(exchange -> factory.getAuthContext(exchange.getRequest()))
                    .flatMap(authContext -> Mono.just(authContext)
                        .contextWrite(ctxW -> ctxW.put(AuthContext.class, authContext))));
            /*if (context.isPresent()) {
                return Mono.just(context);
            }

            return deferExchange()
                .map(exchange -> factory.getAuthContext(exchange.getRequest()))
                .flatMap(authContext -> Mono.just(authContext)
                    .contextWrite(ctxW -> ctxW.put(AuthContext.class, authContext)));*/
        });
    }

}
