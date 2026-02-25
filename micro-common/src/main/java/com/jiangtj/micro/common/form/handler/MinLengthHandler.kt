package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import com.jiangtj.micro.common.validation.MinLength
import java.lang.reflect.Field

/**
 * MinLengthHandler 类用于处理 `@MinLength` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `MinLength` 类型的注解。
 * 生成的规则类型为 string，并设置 min 属性为注解指定的最小长度。
 */
class MinLengthHandler : FormRuleHandler<MinLength> {
    override fun annotation() = MinLength::class.java

    override fun handle(field: Field, element: MinLength): FormRule {
        val rule = FormRule()
        rule.type = "string"
        rule.min = element.value
        return rule
    }
}
