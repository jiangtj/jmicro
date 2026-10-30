package __PKG__.auth

import com.jiangtj.micro.auth.BCryptUtils
import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.stereotype.Component

/**
 * 应用账户，包含角色与由角色派生出的权限
 */
data class AppUser(
    val username: String,
    val nickname: String,
    val roles: List<String>,
    val permissions: List<String>,
)

/**
 * 内存账户存储（示例实现）。
 *
 * 账户只保存角色，权限由 [RolePermissionResolver] 按角色解析——与 jmicro demo 的落库实现
 * （`sys_user` / `sys_role` / `sys_permission` 及关联表）保持同一套契约：
 * 查用户 → 校验密码（bcrypt）→ 查角色 → 按角色解析权限。
 *
 * 接入真实持久层时替换本类，并把 [RolePermissionResolver] 换成查库 + 缓存版本
 * （见 `references/auth.md`）。
 */
@Component
class AppUserStore(
    private val rolePermissionResolver: RolePermissionResolver,
) {

    private data class Account(
        val encodedPassword: String,
        val nickname: String,
        val roles: List<String>,
    )

    private val accounts: Map<String, Account> = mapOf(
        "admin" to Account(
            encodedPassword = BCryptUtils.encode("admin123"),
            nickname = "管理员",
            roles = listOf("admin"),
        ),
        "reader" to Account(
            encodedPassword = BCryptUtils.encode("reader123"),
            nickname = "读者",
            roles = listOf("reader"),
        ),
    )

    /**
     * 校验用户名密码，失败（含用户不存在、密码不匹配）抛出 401
     */
    fun login(username: String, password: String): AppUser {
        val account = accounts[username]
        if (account == null || !BCryptUtils.matches(password, account.encodedPassword)) {
            throw BaseExceptionUtils.unauthorized("用户名或密码错误")
        }
        return toUser(username, account)
    }

    fun find(username: String): AppUser? = accounts[username]?.let { toUser(username, it) }

    private fun toUser(username: String, account: Account): AppUser = AppUser(
        username = username,
        nickname = account.nickname,
        roles = account.roles,
        permissions = rolePermissionResolver.resolve(account.roles),
    )
}
