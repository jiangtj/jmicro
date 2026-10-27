package com.jiangtj.micro.demobackend.auth.role

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * 角色新增/修改的请求体
 *
 * `permissionCodes` 为角色绑定的权限编码（取自 `sys_permission` 字典），
 * 修改时整体覆盖，传空数组表示清空该角色的全部权限。
 */
data class RoleForm(
    @field:NotBlank
    @field:Size(max = 64)
    @field:Pattern(
        regexp = "^[A-Za-z0-9_.:-]+$",
        message = "角色编码只能包含字母、数字、下划线、点、冒号和短横线",
    )
    val code: String,
    @field:NotBlank
    @field:Size(max = 64)
    val name: String,
    val permissionCodes: List<String> = emptyList(),
)
