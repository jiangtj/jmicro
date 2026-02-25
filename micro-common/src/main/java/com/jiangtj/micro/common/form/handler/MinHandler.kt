package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Min
import java.lang.reflect.Field

/**
 * MinHandler 类用于处理 `@Min` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Min` 类型的注解。
 * 生成的规则类型为 number，并设置 min 属性为注解指定的最小值。
 */
class MinHandler : FormRuleHandler<Min> {
    override fun annotation() = Min::class.java

    override fun handle(field: Field, element: Min): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.min = element.value.toInt()
        return rule
    }
}
