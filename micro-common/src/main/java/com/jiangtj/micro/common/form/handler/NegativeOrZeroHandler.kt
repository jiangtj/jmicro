package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.NegativeOrZero
import java.lang.reflect.Field

/**
 * NegativeOrZeroHandler 类用于处理 `@NegativeOrZero` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `NegativeOrZero` 类型的注解。
 * 生成的规则类型为 number，并设置 max 属性为 0，表示必须为零或负数。
 */
class NegativeOrZeroHandler : FormRuleHandler<NegativeOrZero> {
    override fun annotation() = NegativeOrZero::class.java

    override fun handle(field: Field, element: NegativeOrZero): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.max = 0
        return rule
    }
}
