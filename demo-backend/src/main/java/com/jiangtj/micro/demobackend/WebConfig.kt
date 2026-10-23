package com.jiangtj.micro.demobackend

import com.jiangtj.micro.auth.core.AuthService
import com.jiangtj.micro.web.interceptor.FunctionHandlerInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val authService: AuthService,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(
            FunctionHandlerInterceptor.preHandle { request, _, _ ->
                if ("options".equals(request.method, ignoreCase = true)) {
                    true
                } else {
                    authService.hasLogin()
                    true
                }
            }
        )
            .addPathPatterns("/**")
            .excludePathPatterns("/", "/insecure/**", "/anno/**", "/food/**", "/toLogin", "/login")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedHeaders("*")
            .allowedMethods("*")
    }
}
