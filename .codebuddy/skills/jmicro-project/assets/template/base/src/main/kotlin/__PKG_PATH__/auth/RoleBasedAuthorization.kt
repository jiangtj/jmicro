package __PKG__.auth

import com.jiangtj.micro.auth.context.Authorization

/**
 * 只携带角色的鉴权信息（与 jmicro demo 一致）。
 *
 * JWT 中不保存权限：`permissions()` 在首次被访问（如 `@HasPermission` 校验、`GET /me`）时
 * 才通过 [RolePermissionResolver] 按角色解析，所以权限点变多也不会撑大 token，
 * 调整角色权限后已签发的 token 无需重新登录即可生效。
 */
class RoleBasedAuthorization(
    private val roles: List<String>,
    private val resolvePermissions: (Collection<String>) -> List<String>,
) : Authorization {

    private val permissions: List<String> by lazy { resolvePermissions(roles) }

    override fun roles(): List<String> = roles

    override fun permissions(): List<String> = permissions
}
