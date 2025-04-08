package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.context.AuthRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Collections;
import java.util.List;

/**
 * WebFlux 环境下的 AuthRequest 实现
 */
public record ReactiveAuthRequest(ServerHttpRequest request) implements AuthRequest {

    @Override
    public String getPath() {
        return request.getPath().value();
    }

    @Override
    public List<String> getQueryParams(String name) {
        List<String> params = request.getQueryParams().get(name);
        return params != null ? params : Collections.emptyList();
    }

    @Override
    public List<String> getHeaders(String name) {
        List<String> headers = request.getHeaders().get(name);
        return headers != null ? headers : Collections.emptyList();
    }
}