package com.jiangtj.micro.demobackend.auth.permission

import com.jiangtj.micro.demobackend.common.withDefaultSort
import com.jiangtj.micro.demobackend.jooq.tables.SysPermission as SysPermissionTable
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysPermissionDao
import com.jiangtj.micro.demobackend.jooq.tables.pojos.SysPermission
import com.jiangtj.micro.sql.jooq.SortPropertyStyle
import com.jiangtj.micro.sql.jooq.dao.kt.fetchPage
import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 * 权限字典查询。
 *
 * `sys_permission` 由可重复迁移脚本 `db/migration/R__sys_permission.sql` 维护，
 * 角色与权限的绑定关系存放在 `sys_role_permission`。
 */
@Service
class PermissionService(
    private val permissionDao: SysPermissionDao,
) {

    /**
     * 权限列表，按编码排序；`keyword` 同时匹配编码与名称（忽略大小写）
     */
    fun list(keyword: String?): List<SysPermission> {
        val permissions = permissionDao.findAll().sortedBy { it.code }
        val kw = keyword?.trim()
        if (kw.isNullOrEmpty()) {
            return permissions
        }
        return permissions.filter {
            it.code?.contains(kw, ignoreCase = true) == true ||
                it.name?.contains(kw, ignoreCase = true) == true
        }
    }

    /**
     * 权限分页查询；[keyword] 同时匹配编码与名称（忽略大小写），未指定排序时按编码升序。
     */
    fun page(keyword: String?, pageable: Pageable): Page<SysPermission> {
        val kw = keyword?.trim()
        val condition = if (kw.isNullOrEmpty()) {
            null
        } else {
            SysPermissionTable.SYS_PERMISSION.CODE.containsIgnoreCase(kw)
                .or(SysPermissionTable.SYS_PERMISSION.NAME.containsIgnoreCase(kw))
        }
        return permissionDao.fetchPage()
            .where(condition)
            .pageable(pageable.withDefaultSort("code"), SortPropertyStyle.SNAKE_LOWER)
            .fetchPage()
    }

    fun get(code: String): SysPermission = permissionDao.fetchOneByCode(code)
        ?: throw BaseExceptionUtils.notFound("权限不存在：$code")
}
