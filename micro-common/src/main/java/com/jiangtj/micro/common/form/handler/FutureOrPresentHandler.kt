package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.FutureOrPresent
import java.lang.reflect.Field

/**
 * FutureOrPresentHandler 类用于处理 `@FutureOrPresent` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `FutureOrPresent` 类型的注解。
 * 生成的规则类型为 date，用于验证日期必须是未来或当前的时间。
 */
class FutureOrPresentHandler : FormRuleHandler<FutureOrPresent> {
    override fun annotation() = FutureOrPresent::class.java

    override fun handle(field: Field, element: FutureOrPresent): FormRule {
        val rule = FormRule()
        rule.type = "date"
        return rule
    }
}
