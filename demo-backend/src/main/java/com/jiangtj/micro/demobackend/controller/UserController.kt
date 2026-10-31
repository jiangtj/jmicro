package com.jiangtj.micro.demobackend.controller

import com.jiangtj.micro.auth.annotations.HasRole
import com.jiangtj.micro.demobackend.auth.role.RoleOption
import com.jiangtj.micro.demobackend.auth.role.RoleService
import com.jiangtj.micro.demobackend.auth.user.UserDetail
import com.jiangtj.micro.demobackend.auth.user.UserForm
import com.jiangtj.micro.demobackend.auth.user.UserQuery
import com.jiangtj.micro.demobackend.auth.user.UserService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 用户管理接口（仅 admin 角色可访问），密码只入不出
 *
 * - `GET /users/page`：用户分页查询（含绑定的角色 id）
 * - `GET /users/roles`：角色下拉选项（筛选框用，仅 id/code/name）
 * - `GET /users/{id}`：用户详情
 * - `POST /users`：新增用户（密码必填）
 * - `PUT /users/{id}`：修改用户（密码留空表示不修改，角色绑定整体覆盖）
 * - `DELETE /users/{id}`：删除用户（同时清理用户-角色关联）
 */
@HasRole("admin")
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val roleService: RoleService,
) {

    /**
     * 用户分页查询：过滤参数按 [UserQuery] 声明（`username`/`enabled` 精确匹配，`nickname` 模糊匹配）；
     * 分页参数 `page`（从 1 开始）、`size`、`sortQ`（驼峰属性名，如 `sortQ=createTime,desc`）
     *
     * 直接返回 Spring Data 的 `Page`，由 `spring.data.web.pageable.serialization-mode=VIA_DTO`
     * 序列化为 `{content, page:{size, number, totalElements, totalPages}}`。
     */
    @GetMapping("/page")
    fun page(
        pageable: Pageable,
        @ModelAttribute query: UserQuery,
    ): Page<UserDetail> = userService.page(pageable, query)

    /**
     * 角色下拉选项：供前端筛选框/选择器使用，按 id 升序返回，不含权限明细
     */
    @GetMapping("/roles")
    fun roles(): List<RoleOption> = roleService.list().map { role ->
        RoleOption(id = role.id, code = role.code, name = role.name)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): UserDetail = userService.get(id)

    @PostMapping
    fun create(@Valid @RequestBody form: UserForm): UserDetail = userService.create(form)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody form: UserForm): UserDetail =
        userService.update(id, form)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        userService.delete(id)
    }
}
