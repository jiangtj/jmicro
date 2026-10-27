package com.jiangtj.micro.demobackend.dao

import com.jiangtj.micro.demobackend.jooq.tables.SysRolePermission
import com.jiangtj.micro.demobackend.jooq.tables.SysUserRole
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
     * 查询用户通过角色间接拥有的权限编码，按权限编码排序。
     *
     * `sys_role_permission` 直接保存权限编码（`permission_code`），无需再关联 `sys_permission`。
     */
    fun findCodesByUserId(userId: Long?): List<String> =
        dsl.selectDistinct(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .from(SysUserRole.SYS_USER_ROLE)
            .join(SysRolePermission.SYS_ROLE_PERMISSION)
            .on(SysUserRole.SYS_USER_ROLE.ROLE_ID.eq(SysRolePermission.SYS_ROLE_PERMISSION.ROLE_ID))
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.eq(userId))
            .orderBy(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .fetch(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .filterNotNull()
}
