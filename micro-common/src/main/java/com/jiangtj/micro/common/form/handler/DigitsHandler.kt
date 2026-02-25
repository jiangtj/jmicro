package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Digits
import java.lang.reflect.Field

class DigitsHandler : FormRuleHandler<Digits> {
    override fun annotation() = Digits::class.java

    override fun handle(field: Field, element: Digits): FormRule {
        val rule = FormRule()
        rule.type = "number"
        return rule
    }
}
