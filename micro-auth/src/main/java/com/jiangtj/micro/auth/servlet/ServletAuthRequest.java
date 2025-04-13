package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.context.AuthRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.server.ServletServerHttpRequest.initURI;

/**
 * Servlet 环境下的 AuthRequest 实现
 */
public final class ServletAuthRequest implements AuthRequest {
    private final HttpServletRequest request;
    private URI uri;
    private RequestPath path;

    public ServletAuthRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getPath() {
        if (this.path == null) {
            this.path = RequestPath.parse(getURI(), request.getContextPath());
        }
        return this.path.pathWithinApplication().value();
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

    @Override
    public URI getURI() {
        if (this.uri == null) {
            this.uri = initURI(request);
        }
        return this.uri;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(request.getMethod());
    }

    public HttpServletRequest request() {
        return request;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getSessionAttribute(String name) {
        return (T) request.getSession().getAttribute(name);
    }

}