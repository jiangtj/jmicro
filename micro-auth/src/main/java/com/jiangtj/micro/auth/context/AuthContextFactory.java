package com.jiangtj.micro.auth.context;

import org.springframework.http.HttpRequest;

import java.util.List;

/**
 * AuthContextFactory 类用于创建 AuthContext 对象。
 */
public class AuthContextFactory {

    private final List<AuthContextConverter> converters;
    private final List<AuthContextHandler> handlers;

    public AuthContextFactory(List<AuthContextConverter> converters, List<AuthContextHandler> handlers) {
        this.converters = converters;
        this.handlers = handlers;
    }

    public AuthContext getAuthContext(HttpRequest request) {
        AuthContext ctx = convertRequest(request);
        handlers.forEach(handler -> handler.handle(ctx));
        return ctx;
    }

    private AuthContext convertRequest(HttpRequest request) {
        for (AuthContextConverter converter : converters) {
            AuthContext context = converter.convert(request);
            if (context != null) {
                return context;
            }
        }
        return AuthContext.unLogin();
    }

}
