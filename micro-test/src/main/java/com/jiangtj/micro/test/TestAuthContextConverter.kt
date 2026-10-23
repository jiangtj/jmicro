package com.jiangtj.micro.test

import com.jiangtj.micro.auth.context.AuthContext
import com.jiangtj.micro.auth.context.AuthContextConverter
import com.jiangtj.micro.auth.context.AuthRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Order(Ordered.HIGHEST_PRECEDENCE)
class TestAuthContextConverter : AuthContextConverter {

    override fun convert(request: AuthRequest): AuthContext? = TestAuthContextHolder.getAuthContext()
}
