package __PKG__.auth

import org.springframework.stereotype.Component

/**
 * 按角色解析权限编码（内存实现）。
 *
 * 这是把"权限不进 token"落地的关键组件：JWT 只带角色，鉴权或用 `GET /me` 读取当前用户时
 * 才通过本组件解析权限。demo 中该组件查询 `sys_role_permission` 并用 Caffeine 按角色缓存，
 * 角色权限变更时精确失效（见 `references/auth.md` 的落库版本），对外 API 与这里保持一致：
 * `resolve(roles)` / `invalidate(roleCode)`。
 *
 * 角色数据存在数据库后，用同名同签名的落库实现替换本类即可，调用方无需改动。
 */
@Component
class RolePermissionResolver {

    private val rolePermissions: Map<String, List<String>> = mapOf(
        "admin" to listOf("book:read", "book:write"),
        "reader" to listOf("book:read"),
    )

    /**
     * 合并多个角色拥有的权限编码，去重并按编码排序
     */
    fun resolve(roles: Collection<String>): List<String> = roles
        .filter { it.isNotBlank() }
        .distinct()
        .flatMap { rolePermissions[it] ?: emptyList() }
        .distinct()
        .sorted()

    /**
     * 角色权限变更后失效该角色的缓存。
     * 内存实现没有缓存，落库实现（Caffeine）需要真正清掉对应 key。
     */
    fun invalidate(roleCode: String) {
        // no-op：内存实现的角色权限是常量
    }
}
