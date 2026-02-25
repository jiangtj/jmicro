package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.DecimalMax
import java.lang.reflect.Field

/**
 * DecimalMaxHandler 类用于处理 `@DecimalMax` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `DecimalMax` 类型的注解。
 * 生成的规则类型为 number，并设置 max 属性为注解指定的小数最大值的整数部分。
 */
class DecimalMaxHandler : FormRuleHandler<DecimalMax> {
    override fun annotation() = DecimalMax::class.java

    override fun handle(field: Field, element: DecimalMax): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.max = element.value.toDouble().toInt()
        return rule
    }
}
