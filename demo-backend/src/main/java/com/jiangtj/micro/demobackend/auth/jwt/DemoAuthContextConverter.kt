package com.jiangtj.micro.demobackend.auth.jwt

import com.jiangtj.micro.auth.AuthRequestAttributes
import com.jiangtj.micro.auth.context.AuthContext
import com.jiangtj.micro.auth.context.AuthContextConverter
import com.jiangtj.micro.auth.context.AuthRequest
import com.jiangtj.micro.auth.context.Subject
import com.jiangtj.micro.demobackend.auth.permission.RolePermissionResolver
import io.jsonwebtoken.Claims
import org.springframework.core.Ordered
import org.springframework.stereotype.Component

/**
 * 将请求头中的 JWT 转换为鉴权上下文。
 *
 * token 由 `POST /login` 以 HS256 签发；签名或有效期校验失败时视为未登录。
 * token 中只包含角色，权限由 [RoleBasedAuthorization] 按角色惰性解析（带缓存）。
 * 优先级高于内置的 `JwtConverter`（后者用于 OIDC/JWKS 场景）。
 */
@Component
class DemoAuthContextConverter(
    private val jwtService: DemoJwtService,
    private val rolePermissionResolver: RolePermissionResolver,
) : AuthContextConverter, Ordered {

    override fun getOrder(): Int = 0

    override fun convert(request: AuthRequest): AuthContext {
        val token = request.getHeader(AuthRequestAttributes.TOKEN_HEADER_NAME).orElse(null)
            ?: return AuthContext.unLogin()
        val claims = jwtService.parse(removePrefix(token))
            ?: return AuthContext.unLogin()
        return toContext(claims)
    }

    private fun toContext(claims: Claims): AuthContext {
        val subject = Subject.builder()
            .id(claims.subject)
            .name(claims.subject)
            .displayName(claims.get(DemoJwtService.CLAIM_NAME, String::class.java))
            .type(claims.get(DemoJwtService.CLAIM_TYPE, String::class.java))
            .issuer(claims.issuer)
            .build()
        return AuthContext.create(
            subject,
            RoleBasedAuthorization(jwtService.roles(claims), rolePermissionResolver::resolve),
        )
    }

    private fun removePrefix(token: String): String {
        val prefix = AuthRequestAttributes.TOKEN_HEADER_PREFIX
        return if (token.startsWith(prefix)) token.substring(prefix.length).trim() else token
    }
}
