package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Future
import java.lang.reflect.Field

/**
 * FutureHandler 类用于处理 `@Future` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Future` 类型的注解。
 * 生成的规则类型为 date，用于验证日期必须是未来的时间。
 */
class FutureHandler : FormRuleHandler<Future> {
    override fun annotation() = Future::class.java

    override fun handle(field: Field, element: Future): FormRule {
        val rule = FormRule()
        rule.type = "date"
        return rule
    }
}
