package __PKG__.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

/**
 * 基于 HS256 的 JWT 签发与校验。
 *
 * 只有角色写入 claims，权限不进 token：权限均由角色派生（见 [RolePermissionResolver]），
 * 项目权限点变多后 token 不会膨胀，调整角色权限后也无需重新登录。
 */
@Component
class JwtService(
    private val properties: JwtProperties,
) {

    companion object {
        const val CLAIM_NAME: String = "name"
        const val CLAIM_TYPE: String = "type"
        const val CLAIM_ROLES: String = "roles"

        /**
         * 标识本应用签发的账号体系
         */
        const val SUBJECT_TYPE: String = "__PROJECT_NAME__"
    }

    private val signKey: SecretKey = Keys.hmacShaKeyFor(properties.secret.toByteArray(StandardCharsets.UTF_8))

    /**
     * 为登录成功的用户签发 token（只带角色）
     */
    fun createToken(user: AppUser): String {
        val now = Instant.now()
        return Jwts.builder()
            .issuer(properties.issuer)
            .subject(user.username)
            .claim(CLAIM_NAME, user.nickname)
            .claim(CLAIM_TYPE, SUBJECT_TYPE)
            .claim(CLAIM_ROLES, user.roles)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(properties.ttl)))
            .signWith(signKey, Jwts.SIG.HS256)
            .compact()
    }

    /**
     * 校验签名与有效期，失败（含格式错误、过期）返回 null
     */
    fun parse(token: String): Claims? =
        try {
            Jwts.parser()
                .verifyWith(signKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }

    fun roles(claims: Claims): List<String> = stringList(claims, CLAIM_ROLES)

    private fun stringList(claims: Claims, name: String): List<String> {
        val value = claims[name]
        return if (value is List<*>) value.map { it.toString() } else emptyList()
    }
}
