package com.jiangtj.micro.auth.context;

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

    public AuthContext getAuthContext(AuthRequest request) {
        AuthContext ctx = convertRequest(request);
        handlers.forEach(handler -> handler.handle(ctx));
        return ctx;
    }

    private AuthContext convertRequest(AuthRequest request) {
        for (AuthContextConverter converter : converters) {
            AuthContext context = converter.convert(request);
            if (context != null) {
                return context;
            }
        }
        return AuthContext.unLogin();
    }

}
