package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Positive
import java.lang.reflect.Field

class PositiveHandler : FormRuleHandler<Positive> {
    override fun annotation() = Positive::class.java

    override fun handle(field: Field, element: Positive): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.min = 1
        return rule
    }
}
