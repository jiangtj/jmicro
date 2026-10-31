package com.jiangtj.micro.demobackend.auth.role

import com.jiangtj.micro.demobackend.auth.permission.RolePermissionResolver
import com.jiangtj.micro.demobackend.common.withDefaultSort
import com.jiangtj.micro.demobackend.dao.SysRoleRepository
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysPermissionDao
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysRoleDao
import com.jiangtj.micro.demobackend.jooq.tables.pojos.SysRole
import com.jiangtj.micro.sql.jooq.SortPropertyStyle
import com.jiangtj.micro.sql.jooq.dao.kt.fetchPage
import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 用户角色增删改查。
 *
 * 角色主体存放在 `sys_role`（持久层使用 jOOQ 生成的 `SysRoleDao`），
 * 角色与权限的绑定存放在 `sys_role_permission`，按权限编码关联（权限字典见 `sys_permission`）。
 *
 * 角色权限变更后会失效 [RolePermissionResolver] 中对应角色的缓存，保证鉴权立即生效。
 */
@Service
class RoleService(
    private val roleDao: SysRoleDao,
    private val permissionDao: SysPermissionDao,
    private val roleRepository: SysRoleRepository,
    private val rolePermissionResolver: RolePermissionResolver,
) {

    /**
     * 角色列表（含各自绑定的权限编码）
     */
    fun list(): List<RoleDetail> {
        val roles = roleDao.findAll().sortedBy { it.id }
        val permissions = roleRepository.findPermissionCodesByRoleIds(roles.mapNotNull { it.id })
        return roles.map { role -> role.toDetail(permissions[role.id] ?: emptyList()) }
    }

    /**
     * 角色分页查询（含各自绑定的权限编码）。
     *
     * 排序字段按驼峰属性名传入，如 `sortQ=code,desc`，未指定时按主键升序。
     */
    fun page(pageable: Pageable): Page<RoleDetail> {
        val page = roleDao.fetchPage()
            .pageable(pageable.withDefaultSort("id"), SortPropertyStyle.SNAKE_LOWER)
            .fetchPage<SysRole>()
        // 批量查询当前页角色的权限编码，避免 N+1 查询
        val permissions = roleRepository.findPermissionCodesByRoleIds(page.content.mapNotNull { it.id })
        return page.map { role -> role.toDetail(permissions[role.id] ?: emptyList()) }
    }

    fun get(id: Long): RoleDetail {
        val role = findRole(id)
        return role.toDetail(roleRepository.findPermissionCodesByRoleId(id))
    }

    @Transactional
    fun create(form: RoleForm): RoleDetail {
        val code = form.code.trim()
        checkCodeAvailable(code, null)
        val permissionCodes = checkPermissionCodes(form.permissionCodes)
        val role = SysRole(id = null, code = code, name = form.name.trim())
        roleDao.insert(role)
        val id = requireNotNull(role.id) { "角色主键未生成" }
        roleRepository.replacePermissions(id, permissionCodes)
        return role.toDetail(permissionCodes)
    }

    @Transactional
    fun update(id: Long, form: RoleForm): RoleDetail {
        val role = findRole(id)
        val oldCode = role.code
        val code = form.code.trim()
        checkCodeAvailable(code, id)
        val permissionCodes = checkPermissionCodes(form.permissionCodes)
        role.code = code
        role.name = form.name.trim()
        roleDao.update(role)
        roleRepository.replacePermissions(id, permissionCodes)
        // 权限绑定或角色编码变化后失效缓存，旧编码的缓存也要清掉
        oldCode?.let(rolePermissionResolver::invalidate)
        rolePermissionResolver.invalidate(code)
        return role.toDetail(permissionCodes)
    }

    /**
     * 删除角色，并清理 `sys_user_role` 与 `sys_role_permission` 中该角色的关联数据
     * （关联表未建外键，关联由应用层维护）
     */
    @Transactional
    fun delete(id: Long) {
        val role = findRole(id)
        roleRepository.deleteRelations(id)
        roleDao.deleteById(id)
        role.code?.let(rolePermissionResolver::invalidate)
    }

    private fun findRole(id: Long): SysRole = roleDao.fetchOneById(id)
        ?: throw BaseExceptionUtils.notFound("角色不存在：$id")

    /**
     * 角色编码全局唯一：新增时不允许重复，修改时排除自身
     */
    private fun checkCodeAvailable(code: String, selfId: Long?) {
        val exists = roleDao.fetchOneByCode(code) ?: return
        if (exists.id != selfId) {
            throw BaseExceptionUtils.badRequest("角色编码已存在：$code")
        }
    }

    /**
     * 校验权限编码是否存在于 `sys_permission` 字典中，去空、去重并保持入参顺序
     */
    private fun checkPermissionCodes(codes: List<String>): List<String> {
        val targets = codes.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        if (targets.isEmpty()) {
            return emptyList()
        }
        val known = permissionDao.fetchByCode(*targets.toTypedArray()).mapNotNull { it.code }.toSet()
        val unknown = targets.filterNot { it in known }
        if (unknown.isNotEmpty()) {
            throw BaseExceptionUtils.badRequest("未知的权限编码：${unknown.joinToString(", ")}")
        }
        return targets
    }

    private fun SysRole.toDetail(permissionCodes: List<String>): RoleDetail = RoleDetail(
        id = requireNotNull(id) { "角色主键为空" },
        code = requireNotNull(code) { "角色编码为空" },
        name = requireNotNull(name) { "角色名称为空" },
        permissionCodes = permissionCodes,
    )
}
