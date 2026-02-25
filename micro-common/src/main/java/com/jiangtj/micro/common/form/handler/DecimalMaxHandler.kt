package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.DecimalMax
import java.lang.reflect.Field

class DecimalMaxHandler : FormRuleHandler<DecimalMax> {
    override fun annotation() = DecimalMax::class.java

    override fun handle(field: Field, element: DecimalMax): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.max = element.value.toDouble().toInt()
        return rule
    }
}
