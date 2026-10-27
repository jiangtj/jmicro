package com.jiangtj.micro.demobackend.dao

import com.jiangtj.micro.demobackend.jooq.tables.SysRole
import com.jiangtj.micro.demobackend.jooq.tables.SysRolePermission
import com.jiangtj.micro.demobackend.jooq.tables.SysUserRole
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * `sys_role` 的自定义查询，用于补充 jOOQ 生成的 `SysRoleDao` 未覆盖的关联查询。
 */
@Repository
class SysRoleRepository(
    private val dsl: DSLContext,
) {

    /**
     * 查询用户拥有的角色编码，按角色 id 排序
     */
    fun findCodesByUserId(userId: Long?): List<String> =
        dsl.select(SysRole.SYS_ROLE.CODE)
            .from(SysUserRole.SYS_USER_ROLE)
            .join(SysRole.SYS_ROLE).on(SysUserRole.SYS_USER_ROLE.ROLE_ID.eq(SysRole.SYS_ROLE.ID))
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.eq(userId))
            .orderBy(SysRole.SYS_ROLE.ID)
            .fetch(SysRole.SYS_ROLE.CODE)
            .filterNotNull()

    /**
     * 查询角色绑定的权限编码，按权限编码排序
     */
    fun findPermissionCodesByRoleId(roleId: Long): List<String> =
        dsl.select(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .from(SysRolePermission.SYS_ROLE_PERMISSION)
            .where(SysRolePermission.SYS_ROLE_PERMISSION.ROLE_ID.eq(roleId))
            .orderBy(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .fetch(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .filterNotNull()

    /**
     * 批量查询多个角色绑定的权限编码，避免角色列表出现 N+1 查询
     */
    fun findPermissionCodesByRoleIds(roleIds: Collection<Long>): Map<Long, List<String>> {
        if (roleIds.isEmpty()) {
            return emptyMap()
        }
        return dsl.select(
            SysRolePermission.SYS_ROLE_PERMISSION.ROLE_ID,
            SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE,
        )
            .from(SysRolePermission.SYS_ROLE_PERMISSION)
            .where(SysRolePermission.SYS_ROLE_PERMISSION.ROLE_ID.`in`(roleIds))
            .orderBy(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE)
            .fetch()
            .mapNotNull { record ->
                record.value1()?.let { roleId ->
                    record.value2()?.let { code -> roleId to code }
                }
            }
            .groupBy({ it.first }, { it.second })
    }

    /**
     * 覆盖写入角色的权限绑定：先删除该角色的旧绑定，再按入参顺序插入
     */
    fun replacePermissions(roleId: Long, permissionCodes: Collection<String>) {
        dsl.deleteFrom(SysRolePermission.SYS_ROLE_PERMISSION)
            .where(SysRolePermission.SYS_ROLE_PERMISSION.ROLE_ID.eq(roleId))
            .execute()
        permissionCodes.forEach { code ->
            dsl.insertInto(SysRolePermission.SYS_ROLE_PERMISSION)
                .set(SysRolePermission.SYS_ROLE_PERMISSION.ROLE_ID, roleId)
                .set(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_CODE, code)
                .execute()
        }
    }

    /**
     * 清理角色关联数据（用户-角色、角色-权限）。
     *
     * 两张关联表未建外键，关联由应用层维护，删除角色前需显式清理，避免产生孤立记录。
     */
    fun deleteRelations(roleId: Long) {
        dsl.deleteFrom(SysRolePermission.SYS_ROLE_PERMISSION)
            .where(SysRolePermission.SYS_ROLE_PERMISSION.ROLE_ID.eq(roleId))
            .execute()
        dsl.deleteFrom(SysUserRole.SYS_USER_ROLE)
            .where(SysUserRole.SYS_USER_ROLE.ROLE_ID.eq(roleId))
            .execute()
    }
}
