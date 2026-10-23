package com.jiangtj.micro.demobackend.controller

import com.jiangtj.micro.auth.annotations.HasLogin
import com.jiangtj.micro.auth.core.AuthService
import com.jiangtj.micro.demobackend.auth.DemoJwtService
import com.jiangtj.micro.demobackend.auth.DemoUserStore
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * demo 登录接口（/login 已在 WebConfig 中排除登录校验）
 */
@RestController
class LoginController(
    private val userStore: DemoUserStore,
    private val jwtService: DemoJwtService,
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResult {
        val user = userStore.login(request.username, request.password)
        val token = jwtService.createToken(user)
        return LoginResult(token, user.username, user.nickname, user.roles, user.permissions)
    }

    @HasLogin
    @GetMapping("/me")
    fun me(): MeResult {
        val subject = authService.subject
        val authorization = authService.authorization
        return MeResult(subject.id, subject.displayName, authorization.roles(), authorization.permissions())
    }

    data class LoginRequest(
        @field:NotBlank val username: String,
        @field:NotBlank val password: String,
    )

    data class LoginResult(
        val token: String,
        val username: String,
        val nickname: String,
        val roles: List<String>,
        val permissions: List<String>,
    )

    data class MeResult(
        val username: String,
        val nickname: String,
        val roles: List<String>,
        val permissions: List<String>,
    )
}
