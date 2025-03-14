package com.jiangtj.platform.baseservlet;

import com.jiangtj.micro.auth.core.AuthService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Deprecated
public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Resource
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object o) throws Exception {
        if ("options".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        authService.hasLogin();
        return true;
    }

}
