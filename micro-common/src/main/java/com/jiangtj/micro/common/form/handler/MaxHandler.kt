package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Max
import java.lang.reflect.Field

/**
 * MaxHandler 类用于处理 `@Max` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Max` 类型的注解。
 * 生成的规则类型为 number，并设置 max 属性为注解指定的最大值。
 */
class MaxHandler : FormRuleHandler<Max> {
    override fun annotation() = Max::class.java

    override fun handle(field: Field, element: Max): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.max = element.value.toInt()
        return rule
    }
}
