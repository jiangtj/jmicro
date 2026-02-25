package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.NegativeOrZero
import java.lang.reflect.Field

class NegativeOrZeroHandler : FormRuleHandler<NegativeOrZero> {
    override fun annotation() = NegativeOrZero::class.java

    override fun handle(field: Field, element: NegativeOrZero): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.max = 0
        return rule
    }
}
