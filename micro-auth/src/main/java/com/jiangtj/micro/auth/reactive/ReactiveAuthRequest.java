package com.jiangtj.micro.auth.reactive;

import com.jiangtj.micro.auth.context.AuthRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.server.WebSession;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * WebFlux 环境下的 AuthRequest 实现
 */
public record ReactiveAuthRequest(ServerHttpRequest request, WebSession session) implements AuthRequest {

    @Override
    public String getPath() {
        return request.getPath().pathWithinApplication().value();
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

    @Override
    public URI getURI() {
        return request.getURI();
    }

    @Override
    public HttpMethod getMethod() {
        return request.getMethod();
    }

    @Nullable
    @Override
    public Object getSessionAttribute(String name) {
        return session.getAttribute(name);
    }
}