package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Digits
import java.lang.reflect.Field

/**
 * DigitsHandler 类用于处理 `@Digits` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Digits` 类型的注解。
 * 生成的规则类型为 number，用于验证数字的整数位数和小数位数。
 */
class DigitsHandler : FormRuleHandler<Digits> {
    override fun annotation() = Digits::class.java

    override fun handle(field: Field, element: Digits): FormRule {
        val rule = FormRule()
        rule.type = "number"
        return rule
    }
}
