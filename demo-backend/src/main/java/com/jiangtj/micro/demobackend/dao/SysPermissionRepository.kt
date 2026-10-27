package com.jiangtj.micro.demobackend.dao

import com.jiangtj.micro.demobackend.jooq.tables.SysRole
import com.jiangtj.micro.demobackend.jooq.tables.SysRolePermission
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * `sys_permission` / `sys_role_permission` 的自定义查询，用于补充 jOOQ 生成的 DAO 未覆盖的关联查询。
 */
@Repository
class SysPermissionRepository(
    private val dsl: DSLContext,
) {

    /**
     * 查询给定角色编码拥有的权限编码，按权限编码排序。
     *
     * `sys_role_permission` 直接保存权限编码（`permission_code`），通过 `sys_role.code` 关联角色。
     */
    fun findCodesByRoleCodes(roleCodes: Collection<String>): List<String> {
        if (roleCodes.isEmpty()) {
            return emptyList()
        }
        return dsl.selectDistinct(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .from(SysRolePermission.SYS_ROLE_PERMISSION)
            .join(SysRole.SYS_ROLE).on(SysRole.SYS_ROLE.ID.eq(SysRolePermission.SYS_ROLE_PERMISSION.ROLE_ID))
            .where(SysRole.SYS_ROLE.CODE.`in`(roleCodes))
            .orderBy(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .fetch(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .filterNotNull()
    }
}
