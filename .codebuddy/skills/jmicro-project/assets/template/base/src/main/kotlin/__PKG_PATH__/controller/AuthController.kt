package __PKG__.controller

import com.jiangtj.micro.auth.annotations.HasLogin
import com.jiangtj.micro.auth.core.AuthService
import __PKG__.auth.AppUserStore
import __PKG__.auth.JwtService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * 登录与当前用户信息。
 *
 * `/login` 未标注鉴权注解，因此匿名可访问；其余接口按需标注 `@HasLogin` / `@HasRole` / `@HasPermission`。
 */
@RestController
class AuthController(
    private val userStore: AppUserStore,
    private val jwtService: JwtService,
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResult {
        val user = userStore.login(request.username, request.password)
        return LoginResult(
            token = jwtService.createToken(user),
            username = user.username,
            nickname = user.nickname,
            roles = user.roles,
            permissions = user.permissions,
        )
    }

    @HasLogin
    @GetMapping("/me")
    fun me(): MeResult {
        val subject = authService.subject
        val authorization = authService.authorization
        return MeResult(
            username = subject.id,
            nickname = subject.displayName,
            roles = authorization.roles(),
            permissions = authorization.permissions(),
        )
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
        val username: String?,
        val nickname: String?,
        val roles: List<String>,
        val permissions: List<String>,
    )
}
