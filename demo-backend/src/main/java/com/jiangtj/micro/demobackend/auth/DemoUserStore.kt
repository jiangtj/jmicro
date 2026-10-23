package com.jiangtj.micro.demobackend.auth

import com.jiangtj.micro.auth.BCryptUtils
import com.jiangtj.micro.demobackend.jooq.tables.SysPermission
import com.jiangtj.micro.demobackend.jooq.tables.SysRole
import com.jiangtj.micro.demobackend.jooq.tables.SysRolePermission
import com.jiangtj.micro.demobackend.jooq.tables.SysUserRole
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysUserDao
import com.jiangtj.micro.web.BaseExceptionUtils
import org.jooq.DSLContext
import org.springframework.stereotype.Component

/**
 * demo 账户存储，账号、角色、权限均持久化在数据库中：
 * `sys_user` / `sys_role` / `sys_permission` 及两张关联表。
 *
 * 密码以 bcrypt 哈希保存，校验时通过 [BCryptUtils] 比对；
 * 登录态由 [DemoJwtService] 签发的 JWT 承载，服务端不保存会话。
 */
@Component
open class DemoUserStore(
    private val userDao: SysUserDao,
    private val dsl: DSLContext,
) {

    /**
     * 校验用户名密码，失败（含用户不存在、已禁用、密码不匹配）抛出 401
     */
    fun login(username: String, password: String): DemoUser {
        val user = userDao.fetchOneByUsername(username)
        val encoded = user?.password
        if (user == null || user.enabled != true || encoded == null || !BCryptUtils.matches(password, encoded)) {
            throw BaseExceptionUtils.unauthorized("用户名或密码错误")
        }
        return DemoUser(
            user.username,
            encoded,
            user.nickname,
            findRoles(user.id),
            findPermissions(user.id),
        )
    }

    /**
     * 查询用户拥有的角色编码
     */
    fun findRoles(userId: Long?): List<String> =
        dsl.select(SysRole.SYS_ROLE.CODE)
            .from(SysUserRole.SYS_USER_ROLE)
            .join(SysRole.SYS_ROLE).on(SysUserRole.SYS_USER_ROLE.ROLE_ID.eq(SysRole.SYS_ROLE.ID))
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.eq(userId))
            .orderBy(SysRole.SYS_ROLE.ID)
            .fetch(SysRole.SYS_ROLE.CODE)
            .filterNotNull()

    /**
     * 查询用户通过角色间接拥有的权限编码
     */
    fun findPermissions(userId: Long?): List<String> =
        dsl.selectDistinct(SysPermission.SYS_PERMISSION.CODE)
            .from(SysUserRole.SYS_USER_ROLE)
            .join(SysRolePermission.SYS_ROLE_PERMISSION)
            .on(SysUserRole.SYS_USER_ROLE.ROLE_ID.eq(SysRolePermission.SYS_ROLE_PERMISSION.ROLE_ID))
            .join(SysPermission.SYS_PERMISSION)
            .on(SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION_ID.eq(SysPermission.SYS_PERMISSION.ID))
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.eq(userId))
            .orderBy(SysPermission.SYS_PERMISSION.CODE)
            .fetch(SysPermission.SYS_PERMISSION.CODE)
            .filterNotNull()
}
