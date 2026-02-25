package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Email
import java.lang.reflect.Field

/**
 * EmailHandler 类用于处理 `@Email` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Email` 类型的注解。
 * 生成的规则类型为 email，用于验证邮箱格式。
 */
class EmailHandler : FormRuleHandler<Email> {
    override fun annotation() = Email::class.java

    override fun handle(field: Field, element: Email): FormRule {
        val rule = FormRule()
        rule.type = "email"
        return rule
    }
}
