package com.jiangtj.micro.demobackend.controller

import com.jiangtj.micro.auth.annotations.HasRole
import com.jiangtj.micro.demobackend.auth.role.RoleDetail
import com.jiangtj.micro.demobackend.auth.role.RoleForm
import com.jiangtj.micro.demobackend.auth.role.RoleService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 用户角色管理接口（仅 admin 角色可访问）
 *
 * - `GET /roles/page`：角色分页查询（含绑定的权限编码）
 * - `GET /roles/{id}`：角色详情
 * - `POST /roles`：新增角色
 * - `PUT /roles/{id}`：修改角色（权限绑定整体覆盖）
 * - `DELETE /roles/{id}`：删除角色（同时清理用户-角色、角色-权限关联）
 */
@HasRole("admin")
@RestController
@RequestMapping("/roles")
class RoleController(
    private val roleService: RoleService,
) {

    /**
     * 角色分页查询（含绑定的权限编码）；
     * 分页参数 `page`（从 1 开始）、`size`、`sortQ`（驼峰属性名，如 `sortQ=code,asc`）
     */
    @GetMapping("/page")
    fun page(pageable: Pageable): Page<RoleDetail> = roleService.page(pageable)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): RoleDetail = roleService.get(id)

    @PostMapping
    fun create(@Valid @RequestBody form: RoleForm): RoleDetail = roleService.create(form)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody form: RoleForm): RoleDetail =
        roleService.update(id, form)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        roleService.delete(id)
    }
}
