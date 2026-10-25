package __PKG__.auth

import com.jiangtj.micro.auth.BCryptUtils
import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.stereotype.Component

/**
 * 应用账户，包含角色与权限
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
 * 接入真实持久层时替换本类：查询用户 → 校验密码（bcrypt）→ 查出角色/权限编码。
 */
@Component
class AppUserStore {

    private data class Account(val user: AppUser, val encodedPassword: String)

    private val accounts: Map<String, Account> = listOf(
        AppUser(
            username = "admin",
            nickname = "管理员",
            roles = listOf("admin"),
            permissions = listOf("book:read", "book:write", "book:delete"),
        ),
        AppUser(
            username = "reader",
            nickname = "读者",
            roles = listOf("reader"),
            permissions = listOf("book:read"),
        ),
    ).associate { user ->
        val raw = if (user.username == "admin") "admin123" else "reader123"
        user.username to Account(user, BCryptUtils.encode(raw))
    }

    /**
     * 校验用户名密码，失败（含用户不存在、密码不匹配）抛出 401
     */
    fun login(username: String, password: String): AppUser {
        val account = accounts[username]
        if (account == null || !BCryptUtils.matches(password, account.encodedPassword)) {
            throw BaseExceptionUtils.unauthorized("用户名或密码错误")
        }
        return account.user
    }

    fun find(username: String): AppUser? = accounts[username]?.user
}
