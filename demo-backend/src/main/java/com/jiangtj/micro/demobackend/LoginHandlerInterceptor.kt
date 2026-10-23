package com.jiangtj.micro.demobackend

import com.jiangtj.micro.auth.core.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

@Deprecated("请使用 WebConfig 中的 FunctionHandlerInterceptor")
class LoginHandlerInterceptor(
    private val authService: AuthService,
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if ("options".equals(request.method, ignoreCase = true)) {
            return true
        }

        authService.hasLogin()
        return true
    }
}
