package com.jiangtj.micro.demobackend.auth.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * 用户新增/修改的请求体。
 *
 * `password` 在新增时必填（由 `UserService` 校验），修改时留空表示不修改密码；
 * `roleIds` 为用户绑定的角色（取自 `sys_role` 主键），修改时整体覆盖，传空数组表示清空该用户的全部角色。
 */
data class UserForm(
    @field:NotBlank
    @field:Size(max = 64)
    @field:Pattern(
        regexp = "^[A-Za-z0-9_.-]+$",
        message = "用户名只能包含字母、数字、下划线、点和短横线",
    )
    val username: String,
    /** 明文密码，服务端使用 bcrypt 哈希后存储 */
    @field:Size(min = 6, max = 64, message = "密码长度需在 6-64 之间")
    val password: String? = null,
    /** 昵称，留空时使用用户名 */
    @field:Size(max = 64)
    val nickname: String? = null,
    /** 是否启用，禁用后无法登录 */
    val enabled: Boolean = true,
    val roleIds: List<Long> = emptyList(),
)
