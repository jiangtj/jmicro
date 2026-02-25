package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.DecimalMin
import java.lang.reflect.Field

class DecimalMinHandler : FormRuleHandler<DecimalMin> {
    override fun annotation() = DecimalMin::class.java

    override fun handle(field: Field, element: DecimalMin): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.min = element.value.toDouble().toInt()
        return rule
    }
}
