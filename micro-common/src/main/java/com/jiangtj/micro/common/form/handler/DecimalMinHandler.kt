package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.DecimalMin
import java.lang.reflect.Field

/**
 * DecimalMinHandler 类用于处理 `@DecimalMin` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `DecimalMin` 类型的注解。
 * 生成的规则类型为 number，并设置 min 属性为注解指定的小数最小值的整数部分。
 */
class DecimalMinHandler : FormRuleHandler<DecimalMin> {
    override fun annotation() = DecimalMin::class.java

    override fun handle(field: Field, element: DecimalMin): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.min = element.value.toDouble().toInt()
        return rule
    }
}
