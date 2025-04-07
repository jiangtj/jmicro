package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.AuthRequestAttributes;
import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import jakarta.annotation.Resource;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AuthHolder {

    @Resource
    private AuthContextFactory factory;

    public AuthContext getAuthContext() {
        ServletRequestAttributes requestAttributes = currentRequestAttributes();
        AuthContext context = (AuthContext) requestAttributes.getAttribute(AuthRequestAttributes.AUTH_CONTEXT_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (context == null) {
            context = factory.getAuthContext(new ServletServerHttpRequest(requestAttributes.getRequest()));
            requestAttributes.setAttribute(AuthRequestAttributes.AUTH_CONTEXT_ATTRIBUTE, context, RequestAttributes.SCOPE_REQUEST);
        }
        return context;
    }

    public void setAuthContext(AuthContext ctx) {
        ServletRequestAttributes requestAttributes = currentRequestAttributes();
        requestAttributes.setAttribute(AuthRequestAttributes.AUTH_CONTEXT_ATTRIBUTE, ctx, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * Return the current RequestAttributes instance as ServletRequestAttributes.
     * @see RequestContextHolder#currentRequestAttributes()
     *
     * copy from WebApplicationContextUtils
     */
    private static ServletRequestAttributes currentRequestAttributes() {
        RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
        if (!(requestAttr instanceof ServletRequestAttributes servletRequestAttributes)) {
            throw new IllegalStateException("Current request is not a servlet request");
        }
        return servletRequestAttributes;
    }

}
