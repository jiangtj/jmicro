package com.jiangtj.micro.web.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
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

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = request.getPath().value();
        List<FluentWebFilter> collect = new ArrayList<>();
        this.filters.forEach(filter -> {
            invokeFilter(filter, path, collect);
        });

        if (collect.isEmpty()) {
            return chain.filter(exchange);
        }

        FluentWebFilterChain fluentChain = new FluentWebFilterChain(collect, chain.filter(exchange));
        return fluentChain.filter(exchange);
    }

    private void invokeFilter(FluentWebFilter filter, String path, List<FluentWebFilter> collect) {
        // 存在 exclude，且任意匹配到都不执行过滤
        List<String> exclude = filter.getExclude();
        if (!CollectionUtils.isEmpty(exclude)) {
            boolean anyMatch = exclude.stream()
                    .anyMatch(p -> matcher.match(p, path));
            if (anyMatch)
                return;
        }
        collect.add(filter);
        List<FluentWebFilter.FluentWebFilterPath> paths = filter.getPaths();
        if (!CollectionUtils.isEmpty(paths)) {
            paths.forEach(filterPath -> {
                List<String> include = filterPath.getInclude();
                // 需要 include，且任意匹配到都不执行过滤
                if (CollectionUtils.isEmpty(include)) {
                    return;
                }
                // 任意匹配到都需要执行过滤
                boolean anyMatch = include.stream()
                        .anyMatch(p -> matcher.match(p, path));
                if (anyMatch) {
                    invokeFilter(filterPath.getNest(), path, collect);
                }
            });
        }
    }

}
