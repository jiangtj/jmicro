package com.jiangtj.micro.demobackend.auth.user

import com.jiangtj.micro.auth.BCryptUtils
import com.jiangtj.micro.demobackend.dao.SysPermissionRepository
import com.jiangtj.micro.demobackend.dao.SysRoleRepository
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysUserDao
import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.stereotype.Component

/**
 * demo 账户存储，账号、角色、权限均持久化在数据库中：
 * `sys_user` / `sys_role` / `sys_permission` 及两张关联表。
 *
 * 密码以 bcrypt 哈希保存，校验时通过 [BCryptUtils] 比对；
 * 登录态由 `auth.jwt.DemoJwtService` 签发的 JWT 承载，服务端不保存会话。
 */
@Component
open class DemoUserStore(
    private val userDao: SysUserDao,
    private val roleRepository: SysRoleRepository,
    private val permissionRepository: SysPermissionRepository,
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
            username = username,
            password = encoded,
            nickname = user.nickname ?: username,
            roles = findRoles(user.id),
            permissions = findPermissions(user.id),
        )
    }

    /**
     * 查询用户拥有的角色编码
     */
    fun findRoles(userId: Long?): List<String> = roleRepository.findCodesByUserId(userId)

    /**
     * 查询用户通过角色间接拥有的权限编码
     */
    fun findPermissions(userId: Long?): List<String> = permissionRepository.findCodesByUserId(userId)
}
