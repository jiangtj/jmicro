package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import com.jiangtj.micro.web.Orders;
import jakarta.annotation.Resource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(Orders.BASE_EXCEPTION_FILTER + 10)
public class ReactiveAuthContextFilter implements WebFilter {

    @Resource
    private AuthContextFactory factory;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        return exchange.getSession()
            .map(session -> factory.getAuthContext(new ReactiveAuthRequest(request, session)))
            .flatMap(context -> chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(AuthContext.class, context)));
    }
}
