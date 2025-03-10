package com.jiangtj.micro.auth.servlet;

import com.jiangtj.micro.web.Orders;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Order(Orders.BASE_EXCEPTION_FILTER + 20)
public class ServletAuthContextFilter extends OncePerRequestFilter {

    @Resource
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("options".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        authService.initAuthContext(new ServletServerHttpRequest(request));
        filterChain.doFilter(request, response);
    }
}
