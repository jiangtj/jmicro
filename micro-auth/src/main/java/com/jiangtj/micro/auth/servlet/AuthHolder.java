package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.AuthRequestAttributes;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

public class AuthHolder {

    @Resource
    private HttpServletRequest request;
    @Resource
    private AuthContextFactory factory;

    public AuthContext getAuthContext() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Objects.requireNonNull(requestAttributes);
        AuthContext context = (AuthContext) requestAttributes.getAttribute(AuthRequestAttributes.AUTH_CONTEXT_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (context == null) {
            context = factory.getAuthContext(new ServletServerHttpRequest(request));
            setAuthContext(context);
        }
        return context;
    }

    public void setAuthContext(AuthContext ctx) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Objects.requireNonNull(requestAttributes);
        requestAttributes.setAttribute(AuthRequestAttributes.AUTH_CONTEXT_ATTRIBUTE, ctx, RequestAttributes.SCOPE_REQUEST);
    }

}
