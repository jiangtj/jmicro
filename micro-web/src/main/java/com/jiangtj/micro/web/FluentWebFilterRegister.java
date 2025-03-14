package com.jiangtj.micro.web;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class FluentWebFilterRegister implements WebFilter {

    private final List<FluentWebFilter> filters;

    AntPathMatcher matcher = new AntPathMatcher();

    public FluentWebFilterRegister(List<FluentWebFilter> filters) {
        this.filters = filters;
    }

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = request.getPath().value();
        List<FluentWebFilter> collect = new ArrayList<>();
        this.filters.forEach(filter -> {
            invokeFilter(filter, path, collect);
        });

        FluentWebFilterChain fluentChain = new FluentWebFilterChain(collect, chain.filter(exchange));
        return fluentChain.filter(exchange);
    }

    private void invokeFilter(FluentWebFilter filter, String path, List<FluentWebFilter> collect) {
        List<String> exclude = filter.getExclude();
        if (!CollectionUtils.isEmpty(exclude)) {
            for (String ex: exclude) {
                if (matcher.match(ex, path)) {
                    return;
                }
            }
        }
        collect.add(filter);
        List<FluentWebFilter.FluentWebFilterPath> paths = filter.getPaths();
        if (!CollectionUtils.isEmpty(paths)) {
            paths.forEach(filterPath -> {
                for (String in: filterPath.getInclude()) {
                    if (matcher.match(in, path)) {
                        invokeFilter(filterPath.getNest(), path, collect);
                        return;
                    }
                }
            });
        }
    }

}
