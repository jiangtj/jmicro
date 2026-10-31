package com.jiangtj.micro.demobackend.dao

import com.jiangtj.micro.demobackend.jooq.tables.SysUserRole
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * `sys_user` 的自定义查询，用于补充 jOOQ 生成的 `SysUserDao` 未覆盖的关联查询。
 */
@Repository
class SysUserRepository(
    private val dsl: DSLContext,
) {

    /**
     * 查询用户绑定的角色 id，按角色 id 排序
     */
    fun findRoleIdsByUserId(userId: Long): List<Long> =
        dsl.select(SysUserRole.SYS_USER_ROLE.ROLE_ID)
            .from(SysUserRole.SYS_USER_ROLE)
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.eq(userId))
            .orderBy(SysUserRole.SYS_USER_ROLE.ROLE_ID)
            .fetch(SysUserRole.SYS_USER_ROLE.ROLE_ID)
            .filterNotNull()

    /**
     * 批量查询多个用户绑定的角色 id，避免用户列表出现 N+1 查询
     */
    fun findRoleIdsByUserIds(userIds: Collection<Long>): Map<Long, List<Long>> {
        if (userIds.isEmpty()) {
            return emptyMap()
        }
        return dsl.select(
            SysUserRole.SYS_USER_ROLE.USER_ID,
            SysUserRole.SYS_USER_ROLE.ROLE_ID,
        )
            .from(SysUserRole.SYS_USER_ROLE)
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.`in`(userIds))
            .orderBy(SysUserRole.SYS_USER_ROLE.ROLE_ID)
            .fetch()
            .mapNotNull { record ->
                record.value1()?.let { userId ->
                    record.value2()?.let { roleId -> userId to roleId }
                }
            }
            .groupBy({ it.first }, { it.second })
    }

    /**
     * 覆盖写入用户的角色绑定：先删除该用户的旧绑定，再按入参顺序插入
     */
    fun replaceRoles(userId: Long, roleIds: Collection<Long>) {
        dsl.deleteFrom(SysUserRole.SYS_USER_ROLE)
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.eq(userId))
            .execute()
        roleIds.forEach { roleId ->
            dsl.insertInto(SysUserRole.SYS_USER_ROLE)
                .set(SysUserRole.SYS_USER_ROLE.USER_ID, userId)
                .set(SysUserRole.SYS_USER_ROLE.ROLE_ID, roleId)
                .execute()
        }
    }

    /**
     * 清理用户关联数据（用户-角色）。
     *
     * 关联表未建外键，关联由应用层维护，删除用户前需显式清理，避免产生孤立记录。
     */
    fun deleteRelations(userId: Long) {
        dsl.deleteFrom(SysUserRole.SYS_USER_ROLE)
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.eq(userId))
            .execute()
    }
}
