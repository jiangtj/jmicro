package com.jiangtj.platform.baseservlet;

import com.jiangtj.micro.auth.core.AuthService;
import com.jiangtj.micro.spring.boot.servlet.FunctionHandlerInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AuthService authService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(FunctionHandlerInterceptor.preHandle((request, response, handler) -> {
                if ("options".equalsIgnoreCase(request.getMethod())) {
                    return true;
                }
                authService.hasLogin();
                return true;
            }))
            .addPathPatterns("/**")
            .excludePathPatterns("/", "/insecure/**", "/anno/**", "/food/**", "/toLogin", "/login");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("*");
    }
}
