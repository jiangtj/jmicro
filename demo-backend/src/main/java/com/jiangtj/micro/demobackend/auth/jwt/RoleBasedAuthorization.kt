package com.jiangtj.micro.demobackend.auth.jwt

import com.jiangtj.micro.auth.context.Authorization

/**
 * 只携带角色的鉴权信息。
 *
 * JWT 中不再保存权限，`permissions()` 在首次被访问（如 `@HasPermission` 校验、`GET /me`）时
 * 才按角色解析，解析结果由 `RolePermissionResolver` 按角色缓存，所以不会每个请求都查库。
 */
class RoleBasedAuthorization(
    private val roles: List<String>,
    private val resolvePermissions: (Collection<String>) -> List<String>,
) : Authorization {

    private val permissions: List<String> by lazy { resolvePermissions(roles) }

    override fun roles(): List<String> = roles

    override fun permissions(): List<String> = permissions
}
