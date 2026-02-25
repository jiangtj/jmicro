package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Negative
import java.lang.reflect.Field

/**
 * NegativeHandler 类用于处理 `@Negative` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Negative` 类型的注解。
 * 生成的规则类型为 number，并设置 max 属性为 -1，表示必须为负数。
 */
class NegativeHandler : FormRuleHandler<Negative> {
    override fun annotation() = Negative::class.java

    override fun handle(field: Field, element: Negative): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.max = -1
        return rule
    }
}
