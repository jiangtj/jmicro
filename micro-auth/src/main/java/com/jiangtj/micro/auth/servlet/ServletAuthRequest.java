package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.context.AuthRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * Servlet 环境下的 AuthRequest 实现
 */
public record ServletAuthRequest(HttpServletRequest request) implements AuthRequest {

    @Override
    public String getPath() {
        return request.getRequestURI();
    }

    @Override
    public List<String> getQueryParams(String name) {
        String[] values = request.getParameterValues(name);
        return values != null ? List.of(values) : Collections.emptyList();
    }

    @Override
    public List<String> getHeaders(String name) {
        return Collections.list(request.getHeaders(name));
    }
}