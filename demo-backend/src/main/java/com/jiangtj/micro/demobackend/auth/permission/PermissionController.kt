package com.jiangtj.micro.demobackend.auth.permission

import com.jiangtj.micro.auth.annotations.HasRole
import com.jiangtj.micro.demobackend.jooq.tables.pojos.SysPermission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 权限查询接口（仅 admin 角色可访问）
 *
 * - `GET /permissions`：权限字典列表，可用 `keyword` 过滤编码或名称
 * - `GET /permissions/page`：权限分页查询，同样支持 `keyword` 过滤
 * - `GET /permissions/{code}`：按编码查询单个权限
 */
@HasRole("admin")
@RestController
@RequestMapping("/permissions")
class PermissionController(
    private val permissionService: PermissionService,
) {

    @GetMapping
    fun list(@RequestParam(required = false) keyword: String?): List<SysPermission> =
        permissionService.list(keyword)

    /**
     * 权限分页查询；分页参数 `page`（从 0 开始）、`size`、`sort`（驼峰属性名）
     */
    @GetMapping("/page")
    fun page(
        pageable: Pageable,
        @RequestParam(required = false) keyword: String?,
    ): Page<SysPermission> = permissionService.page(keyword, pageable)

    @GetMapping("/{code}")
    fun get(@PathVariable code: String): SysPermission = permissionService.get(code)
}
