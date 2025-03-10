package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import com.jiangtj.micro.auth.context.Authorization;
import com.jiangtj.micro.auth.context.Subject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;

public class AuthService {
    @Resource
    private HttpServletRequest request;
    @Resource
    private AuthContextFactory factory;

    public AuthContext getAuthContext() {
        AuthContext context = AuthHolder.getAuthContext();
        if (context == null) {
            context = factory.getAuthContext(new ServletServerHttpRequest(request));
            AuthHolder.setAuthContext(context);
        }
        return context;
    }

    public Subject getSubject() {
        return getAuthContext().subject();
    }

    public Authorization getAuthorization() {
        return getAuthContext().authorization();
    }

    public void initAuthContext() {
        initAuthContext(new ServletServerHttpRequest(request));
    }

    public void initAuthContext(HttpRequest request) {
        AuthContext context = AuthHolder.getAuthContext();
        if (context == null) {
            context = factory.getAuthContext(request);
            AuthHolder.setAuthContext(context);
        }
    }
}
