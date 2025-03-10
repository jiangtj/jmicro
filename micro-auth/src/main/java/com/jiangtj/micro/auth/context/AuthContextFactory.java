package com.jiangtj.micro.auth.context;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AuthContextFactory 类用于创建 AuthContext 对象。
 */
public class AuthContextFactory {

    private final ObjectProvider<AuthContextConverter> op;
    private List<AuthContextConverter> converters;

    public AuthContextFactory(ObjectProvider<AuthContextConverter> op) {
        this.op = op;
    }

    public void init() {
        this.converters = op.orderedStream()
            .collect(Collectors.toList());
    }

    public AuthContext getAuthContext(HttpRequest request) {
        if (this.converters == null || this.converters.isEmpty()) {
            this.init();
        }

        for (AuthContextConverter converter : this.converters) {
            AuthContext context = converter.convert(request);
            if (context != null) {
                return context;
            }
        }

        return AuthContext.unLogin();
    }

}
