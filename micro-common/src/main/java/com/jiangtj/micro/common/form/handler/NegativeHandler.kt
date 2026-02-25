package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Negative
import java.lang.reflect.Field

class NegativeHandler : FormRuleHandler<Negative> {
    override fun annotation() = Negative::class.java

    override fun handle(field: Field, element: Negative): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.max = -1
        return rule
    }
}
