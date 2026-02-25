package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.PositiveOrZero
import java.lang.reflect.Field

/**
 * PositiveOrZeroHandler 类用于处理 `@PositiveOrZero` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `PositiveOrZero` 类型的注解。
 * 生成的规则类型为 number，并设置 min 属性为 0，表示必须为零或正数。
 */
class PositiveOrZeroHandler : FormRuleHandler<PositiveOrZero> {
    override fun annotation() = PositiveOrZero::class.java

    override fun handle(field: Field, element: PositiveOrZero): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.min = 0
        return rule
    }
}
