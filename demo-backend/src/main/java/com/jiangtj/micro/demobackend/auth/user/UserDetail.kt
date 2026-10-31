package com.jiangtj.micro.demobackend.auth.user

import java.time.LocalDateTime

/**
 * 用户详情（不含密码），`roleIds` 来自 `sys_user_role`
 */
data class UserDetail(
    val id: Long,
    val username: String,
    val nickname: String,
    val enabled: Boolean,
    val roleIds: List<Long>,
    val createTime: LocalDateTime,
)
