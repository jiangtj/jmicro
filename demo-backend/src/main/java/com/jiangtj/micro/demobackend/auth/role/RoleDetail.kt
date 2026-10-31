package com.jiangtj.micro.demobackend.auth.role

/**
 * 角色详情，`permissionCodes` 来自 `sys_role_permission`
 */
data class RoleDetail(
    val id: Long,
    val code: String,
    val name: String,
    val permissionCodes: List<String>,
)

/**
 * 角色下拉选项，供前端筛选框/选择器使用，不携带权限明细
 */
data class RoleOption(
    val id: Long,
    val code: String,
    val name: String,
)
