package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Past
import java.lang.reflect.Field

/**
 * PastHandler 类用于处理 `@Past` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Past` 类型的注解。
 * 生成的规则类型为 date，用于验证日期必须是过去的时间。
 */
class PastHandler : FormRuleHandler<Past> {
    override fun annotation() = Past::class.java

    override fun handle(field: Field, element: Past): FormRule {
        val rule = FormRule()
        rule.type = "date"
        return rule
    }
}
