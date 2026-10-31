package com.jiangtj.micro.demobackend.auth.role

/**
 * 角色分页查询参数。
 *
 * 属性名尽量与 jOOQ 生成的 `SysRole` pojo 保持一致，便于 `conditionByExample` 按属性名自动生成等值条件；
 * 不需要特殊处理的属性（[code]）由 `conditionByExample` 直接生成等值条件；
 * 需要模糊匹配的属性单独处理：
 * - [name]：模糊匹配（忽略大小写）
 */
data class RoleQuery(
    /** 角色编码，精确匹配 */
    var code: String? = null,
    /** 角色名称，模糊匹配 */
    var name: String? = null,
)
