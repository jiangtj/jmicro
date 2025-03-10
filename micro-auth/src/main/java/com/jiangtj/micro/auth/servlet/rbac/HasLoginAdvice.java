package com.jiangtj.micro.auth.servlet.rbac;

import com.jiangtj.micro.auth.context.AuthContext;
import com.jiangtj.micro.auth.context.AuthContextFactory;
import com.jiangtj.micro.auth.servlet.AuthHolder;
import com.jiangtj.micro.auth.servlet.AuthUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.Ordered;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

@Slf4j
public class HasLoginAdvice implements MethodBeforeAdvice, Ordered {

    @Resource
    private HttpServletRequest request;
    @Resource
    private AuthContextFactory factory;

    @Override
    public void before(@NonNull Method method, @NonNull Object[] args, Object target) throws Throwable {
        AuthContext a = AuthHolder.getAuthContext();
        if (a == null) {
            AuthContext authContext = factory.getAuthContext(new ServletServerHttpRequest(request));
            AuthHolder.setAuthContext(authContext);
        }
        AuthUtils.hasLogin();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
