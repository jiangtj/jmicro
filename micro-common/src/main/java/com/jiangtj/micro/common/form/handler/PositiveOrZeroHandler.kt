package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.PositiveOrZero
import java.lang.reflect.Field

class PositiveOrZeroHandler : FormRuleHandler<PositiveOrZero> {
    override fun annotation() = PositiveOrZero::class.java

    override fun handle(field: Field, element: PositiveOrZero): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.min = 0
        return rule
    }
}
