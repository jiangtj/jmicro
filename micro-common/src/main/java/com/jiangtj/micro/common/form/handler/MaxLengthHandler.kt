package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import com.jiangtj.micro.common.validation.MaxLength
import java.lang.reflect.Field

/**
 * MaxLengthHandler 类用于处理 `@MaxLength` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `MaxLength` 类型的注解。
 * 生成的规则类型为 string，并设置 max 属性为注解指定的最大长度。
 */
class MaxLengthHandler : FormRuleHandler<MaxLength> {
    override fun annotation() = MaxLength::class.java

    override fun handle(field: Field, element: MaxLength): FormRule {
        val rule = FormRule()
        rule.type = "string"
        rule.max = element.value
        return rule
    }
}
