package com.jiangtj.micro.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public interface FunctionHandlerInterceptor {

    static HandlerInterceptor preHandle(PreHandleInterceptor interceptor) {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                    @Nullable Object handler) throws Exception {
                return interceptor.preHandle(request, response, handler);
            }
        };
    }

    static HandlerInterceptor postHandle(PostHandleInterceptor interceptor) {
        return new HandlerInterceptor() {
            @Override
            public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                    @NonNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
                interceptor.postHandle(request, response, handler, modelAndView);
            }
        };
    }

    static HandlerInterceptor afterCompletion(AfterCompletionInterceptor interceptor) {
        return new HandlerInterceptor() {
            @Override
            public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                    @NonNull Object handler, @Nullable Exception ex) throws Exception {
                interceptor.afterCompletion(request, response, handler, ex);
            }
        };
    }

}
