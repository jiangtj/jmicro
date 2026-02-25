package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Positive
import java.lang.reflect.Field

/**
 * PositiveHandler 类用于处理 `@Positive` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Positive` 类型的注解。
 * 生成的规则类型为 number，并设置 min 属性为 1，表示必须为正数。
 */
class PositiveHandler : FormRuleHandler<Positive> {
    override fun annotation() = Positive::class.java

    override fun handle(field: Field, element: Positive): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.min = 1
        return rule
    }
}
