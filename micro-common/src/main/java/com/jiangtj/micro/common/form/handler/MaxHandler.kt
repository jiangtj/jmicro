package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Max
import java.lang.reflect.Field

class MaxHandler : FormRuleHandler<Max> {
    override fun annotation() = Max::class.java

    override fun handle(field: Field, element: Max): FormRule {
        val rule = FormRule()
        rule.type = "number"
        rule.max = element.value.toInt()
        return rule
    }
}
