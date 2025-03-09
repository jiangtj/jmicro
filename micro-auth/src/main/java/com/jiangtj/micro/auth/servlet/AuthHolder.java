package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.auth.AuthRequestAttributes;
import com.jiangtj.micro.auth.context.AuthContext;
import jakarta.annotation.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;
import java.util.Optional;

public interface AuthHolder {

    @Nullable
    static AuthContext getAuthContext() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Objects.requireNonNull(requestAttributes);
        return (AuthContext) requestAttributes.getAttribute(AuthRequestAttributes.AUTH_CONTEXT_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }

    static Optional<AuthContext> getAuthContextOptional() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .map(requestAttributes -> requestAttributes.getAttribute(AuthRequestAttributes.AUTH_CONTEXT_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))
            .map(AuthContext.class::cast);
    }

    static void setAuthContext(AuthContext ctx) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Objects.requireNonNull(requestAttributes);
        requestAttributes.setAttribute(AuthRequestAttributes.AUTH_CONTEXT_ATTRIBUTE, ctx, RequestAttributes.SCOPE_REQUEST);
    }

}
