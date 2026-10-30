package __PKG__.auth

import com.jiangtj.micro.auth.AuthRequestAttributes
import com.jiangtj.micro.auth.context.AuthContext
import com.jiangtj.micro.auth.context.AuthContextConverter
import com.jiangtj.micro.auth.context.AuthRequest
import com.jiangtj.micro.auth.context.Subject
import io.jsonwebtoken.Claims
import org.springframework.core.Ordered
import org.springframework.stereotype.Component

/**
 * 把请求头中的 Bearer token 转换为鉴权上下文。
 *
 * 这是**新项目必须自己提供的 Bean**：没有它，所有请求都会被视为未登录。
 * token 由 `POST /login` 签发；签名或有效期校验失败时视为未登录。
 *
 * token 里只有角色，权限由 [RolePermissionResolver] 按角色惰性解析（带缓存），
 * 因此这里构造的是 [RoleBasedAuthorization] 而不是 `Authorization.create(roles, permissions)`。
 */
@Component
class JwtAuthContextConverter(
    private val jwtService: JwtService,
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
            .displayName(claims.get(JwtService.CLAIM_NAME, String::class.java))
            .type(claims.get(JwtService.CLAIM_TYPE, String::class.java))
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
