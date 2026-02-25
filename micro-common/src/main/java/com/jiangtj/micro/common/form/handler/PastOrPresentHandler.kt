package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.PastOrPresent
import java.lang.reflect.Field

/**
 * PastOrPresentHandler 类用于处理 `@PastOrPresent` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `PastOrPresent` 类型的注解。
 * 生成的规则类型为 date，用于验证日期必须是过去或当前的时间。
 */
class PastOrPresentHandler : FormRuleHandler<PastOrPresent> {
    override fun annotation() = PastOrPresent::class.java

    override fun handle(field: Field, element: PastOrPresent): FormRule {
        val rule = FormRule()
        rule.type = "date"
        return rule
    }
}
