package com.jiangtj.micro.demobackend.auth.user

import com.jiangtj.micro.auth.BCryptUtils
import com.jiangtj.micro.demobackend.common.withPageable
import com.jiangtj.micro.demobackend.dao.SysUserRepository
import com.jiangtj.micro.demobackend.jooq.tables.SysUser as SysUserTable
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysRoleDao
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysUserDao
import com.jiangtj.micro.demobackend.jooq.tables.pojos.SysUser
import com.jiangtj.micro.sql.jooq.dao.kt.fetchPage
import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 用户增删改查。
 *
 * 用户主体存放在 `sys_user`（持久层使用 jOOQ 生成的 `SysUserDao`），密码以 bcrypt 哈希保存，
 * 用户与角色的绑定存放在 `sys_user_role`（角色字典见 `sys_role`）。
 *
 * 接口只暴露 [UserDetail]，不会把密码哈希返回给调用方。
 */
@Service
class UserService(
    private val userDao: SysUserDao,
    private val roleDao: SysRoleDao,
    private val userRepository: SysUserRepository,
) {

    /**
     * 用户分页查询（含各自绑定的角色 id）。
     *
     * 排序字段按 pojo 属性名（驼峰）传入，如 `sortQ=createTime,desc`，未指定时按主键倒序。
     *
     * 查询条件优先交给 `conditionByExample`：[UserQuery] 中同名的 pojo 属性（如 [UserQuery.username]、[UserQuery.enabled]）
     * 自动生成等值条件。昵称需要模糊匹配，因此把它作为忽略字段传入，再用 `and` 追加忽略大小写的模糊条件。
     */
    fun page(pageable: Pageable, query: UserQuery = UserQuery()): Page<UserDetail> {
        val table = SysUserTable.SYS_USER
        val page = userDao.fetchPage()
            .conditionByExample(query, table.NICKNAME)
            .and(query.nickname?.trim()?.takeIf { it.isNotEmpty() }?.let { table.NICKNAME.containsIgnoreCase(it) })
            .withPageable(pageable)
            .fetchPage<SysUser>()
        // 批量查询当前页用户的角色 id，避免 N+1 查询
        val roleIds = userRepository.findRoleIdsByUserIds(page.content.mapNotNull { it.id })
        return page.map { user -> user.toDetail(roleIds[user.id] ?: emptyList()) }
    }

    fun get(id: Long): UserDetail {
        val user = findUser(id)
        return user.toDetail(userRepository.findRoleIdsByUserId(id))
    }

    /**
     * 新增用户：用户名唯一，密码必填并以 bcrypt 哈希存储，角色绑定整体写入
     */
    @Transactional
    fun create(form: UserForm): UserDetail {
        val username = form.username.trim()
        checkUsernameAvailable(username, null)
        val password = form.password?.takeIf { it.isNotBlank() }
            ?: throw BaseExceptionUtils.badRequest("密码不能为空")
        val roleIds = checkRoleIds(form.roleIds)
        val user = SysUser(
            id = null,
            username = username,
            password = BCryptUtils.encode(password),
            nickname = nicknameOf(form.nickname, username),
            enabled = form.enabled,
            // 表中 create_time 为 DATETIME(0)，入库前截断纳秒，保证返回值与库中一致
            createTime = LocalDateTime.now().withNano(0),
        )
        userDao.insert(user)
        val id = requireNotNull(user.id) { "用户主键未生成" }
        userRepository.replaceRoles(id, roleIds)
        return user.toDetail(roleIds)
    }

    /**
     * 修改用户：`password` 为空表示保留原密码，角色绑定整体覆盖
     */
    @Transactional
    fun update(id: Long, form: UserForm): UserDetail {
        val user = findUser(id)
        val username = form.username.trim()
        checkUsernameAvailable(username, id)
        val roleIds = checkRoleIds(form.roleIds)
        user.username = username
        user.nickname = nicknameOf(form.nickname, username)
        user.enabled = form.enabled
        form.password?.takeIf { it.isNotBlank() }?.let { user.password = BCryptUtils.encode(it) }
        userDao.update(user)
        userRepository.replaceRoles(id, roleIds)
        return user.toDetail(roleIds)
    }

    /**
     * 删除用户，并清理 `sys_user_role` 中该用户的关联数据
     * （关联表未建外键，关联由应用层维护）
     */
    @Transactional
    fun delete(id: Long) {
        findUser(id)
        userRepository.deleteRelations(id)
        userDao.deleteById(id)
    }

    private fun findUser(id: Long): SysUser = userDao.fetchOneById(id)
        ?: throw BaseExceptionUtils.notFound("用户不存在：$id")

    /**
     * 用户名全局唯一：新增时不允许重复，修改时排除自身
     */
    private fun checkUsernameAvailable(username: String, selfId: Long?) {
        val exists = userDao.fetchOneByUsername(username) ?: return
        if (exists.id != selfId) {
            throw BaseExceptionUtils.badRequest("用户名已存在：$username")
        }
    }

    /**
     * 校验角色 id 是否存在于 `sys_role` 中，去重并保持入参顺序
     */
    private fun checkRoleIds(ids: List<Long>): List<Long> {
        val targets = ids.distinct()
        if (targets.isEmpty()) {
            return emptyList()
        }
        val known = roleDao.fetchById(*targets.toLongArray()).mapNotNull { it.id }.toSet()
        val unknown = targets.filterNot { it in known }
        if (unknown.isNotEmpty()) {
            throw BaseExceptionUtils.badRequest("未知的角色 id：${unknown.joinToString(", ")}")
        }
        return targets
    }

    /** 昵称留空时回退为用户名 */
    private fun nicknameOf(nickname: String?, username: String): String =
        nickname?.trim()?.takeIf { it.isNotEmpty() } ?: username

    private fun SysUser.toDetail(roleIds: List<Long>): UserDetail = UserDetail(
        id = requireNotNull(id) { "用户主键为空" },
        username = requireNotNull(username) { "用户名为空" },
        nickname = nickname ?: username.orEmpty(),
        enabled = enabled ?: true,
        roleIds = roleIds,
        createTime = requireNotNull(createTime) { "创建时间为空" },
    )
}
