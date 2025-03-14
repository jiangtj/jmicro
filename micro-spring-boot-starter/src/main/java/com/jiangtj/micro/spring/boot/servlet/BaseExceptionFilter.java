package com.jiangtj.micro.spring.boot.servlet;

import com.jiangtj.micro.web.Orders;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Order(Orders.BASE_EXCEPTION_FILTER)
public class BaseExceptionFilter extends OncePerRequestFilter {

    @Resource
    ServletExceptionHandler servletExceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException ex) {
            servletExceptionHandler.handle(ex, request, response);
        }
    }
}