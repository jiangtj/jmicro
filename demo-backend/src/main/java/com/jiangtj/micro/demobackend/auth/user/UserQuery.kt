package com.jiangtj.micro.demobackend.auth.user

/**
 * 用户分页查询参数。
 *
 * 属性名尽量与 jOOQ 生成的 `SysUser` pojo 保持一致，便于 `conditionByExample` 按属性名自动生成等值条件；
 * 不需要特殊处理的属性（[username]、[enabled]）由 `conditionByExample` 直接生成等值条件；
 * 需要模糊匹配的属性单独处理：
 * - [nickname]：模糊匹配（忽略大小写）
 */
data class UserQuery(
    /** 用户名，精确匹配 */
    var username: String? = null,
    /** 昵称，模糊匹配 */
    var nickname: String? = null,
    /** 是否启用，精确匹配 */
    var enabled: Boolean? = null,
)
