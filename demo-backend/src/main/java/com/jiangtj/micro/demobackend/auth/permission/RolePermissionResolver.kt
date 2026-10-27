package com.jiangtj.micro.demobackend.auth.permission

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.jiangtj.micro.demobackend.dao.SysPermissionRepository
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * 按角色解析权限编码，并按角色编码缓存解析结果。
 *
 * JWT 中只保存角色，权限在真正需要鉴权（`@HasPermission`）或读取当前用户信息时
 * 才通过本组件解析，因此 token 体积不会随权限点数量增长。
 *
 * 缓存按角色编码为 key，`auth.role.RoleService` 修改/删除角色时会调用 [invalidate] 精确失效，
 * 保证权限调整后立即生效；同时通过最大条数与写入过期时间兜底，避免缓存无上限增长。
 */
@Component
class RolePermissionResolver(
    private val permissionRepository: SysPermissionRepository,
) {

    companion object {
        private const val MAX_SIZE: Long = 1_000
        private val EXPIRE_AFTER_WRITE: Duration = Duration.ofMinutes(5)
    }

    private val cache: Cache<String, List<String>> = Caffeine.newBuilder()
        .maximumSize(MAX_SIZE)
        .expireAfterWrite(EXPIRE_AFTER_WRITE)
        .build()

    /**
     * 合并多个角色拥有的权限编码，去重并按编码排序（未命中缓存的角色会查询数据库）
     */
    fun resolve(roles: Collection<String>): List<String> {
        val targets = roles.filter { it.isNotBlank() }.distinct()
        if (targets.isEmpty()) {
            return emptyList()
        }
        return targets.asSequence()
            .map { role -> cache.get(role) { load(role) } }
            .flatten()
            .distinct()
            .sorted()
            .toList()
    }

    /**
     * 角色权限变更（含角色编码变更、角色删除）后失效该角色的缓存
     */
    fun invalidate(roleCode: String) {
        cache.invalidate(roleCode)
    }

    private fun load(roleCode: String): List<String> =
        permissionRepository.findCodesByRoleCodes(listOf(roleCode))
}
