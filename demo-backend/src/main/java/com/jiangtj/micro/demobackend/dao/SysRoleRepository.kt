package com.jiangtj.micro.demobackend.dao

import com.jiangtj.micro.demobackend.jooq.tables.SysRole
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
}
