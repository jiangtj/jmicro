package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Min
import java.lang.reflect.Field

class MinHandler : FormRuleHandler<Min> {
    override fun annotation() = Min::class.java

    override fun handle(field: Field, element: Min): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.min = element.value.toInt()
        return rule
    }
}
