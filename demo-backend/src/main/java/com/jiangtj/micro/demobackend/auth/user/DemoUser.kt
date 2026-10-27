package com.jiangtj.micro.demobackend.auth.user

/**
 * demo 账户，包含角色与权限
 */
data class DemoUser(
    val username: String,
    val password: String,
    val nickname: String,
    val roles: List<String>,
    val permissions: List<String>,
)
