package com.jiangtj.micro.auth.context;

import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

/**
 * AuthContextFactory 类用于创建 AuthContext 对象。
 */
public class AuthContextFactory {

    private final ObjectProvider<AuthContextConverter> converters;
    private final ObjectProvider<AuthContextHandler> handlers;
    private List<AuthContextConverter> convertersCache;
    private List<AuthContextHandler> handlersCache;

    public AuthContextFactory(ObjectProvider<AuthContextConverter> converters, ObjectProvider<AuthContextHandler> handlers) {
        this.converters = converters;
        this.handlers = handlers;
    }

    public AuthContext getAuthContext(AuthRequest request) {
        AuthContext ctx = convertRequest(request);
        if (handlersCache == null) {
            handlersCache = handlers.orderedStream().toList();
        }
        handlersCache.forEach(handler -> handler.handle(ctx));
        return ctx;
    }

    private AuthContext convertRequest(AuthRequest request) {
        if (convertersCache == null) {
            convertersCache = converters.orderedStream().toList();
        }
        for (AuthContextConverter converter : convertersCache) {
            AuthContext context = converter.convert(request);
            if (context != null) {
                return context;
            }
        }
        return AuthContext.unLogin();
    }

}
