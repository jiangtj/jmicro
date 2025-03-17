package com.jiangtj.micro.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public interface FunctionHandlerInterceptor {

    static HandlerInterceptor preHandle(PreHandleInterceptor interceptor) {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                return interceptor.preHandle(request, response, handler);
            }
        };
    }

    static HandlerInterceptor postHandle(PostHandleInterceptor interceptor) {
        return new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                interceptor.postHandle(request, response, handler, modelAndView);
            }
        };
    }

    static HandlerInterceptor afterCompletion(AfterCompletionInterceptor interceptor) {
        return new HandlerInterceptor() {
            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                interceptor.afterCompletion(request, response, handler, ex);
            }
        };
    }

}
