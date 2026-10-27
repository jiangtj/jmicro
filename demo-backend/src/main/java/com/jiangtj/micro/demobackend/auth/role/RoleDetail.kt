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
