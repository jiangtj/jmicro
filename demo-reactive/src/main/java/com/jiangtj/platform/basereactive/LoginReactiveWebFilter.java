package com.jiangtj.platform.basereactive;

import com.jiangtj.micro.auth.AntPathMatcherUtils;
import com.jiangtj.micro.auth.core.AuthReactiveService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Deprecated
public class LoginReactiveWebFilter implements WebFilter {

    @Resource
    private AuthReactiveService authReactiveService;

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = request.getPath().value();
        if (!AntPathMatcherUtils.match(path,
            List.of("/**"),
            List.of("/", "/insecure/**", "/toLogin", "/login"))) {
            return chain.filter(exchange);
        }

        return authReactiveService.hasLogin()
            .then(chain.filter(exchange));
    }

}
