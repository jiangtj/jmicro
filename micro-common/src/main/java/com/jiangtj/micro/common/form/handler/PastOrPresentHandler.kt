package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.PastOrPresent
import java.lang.reflect.Field

class PastOrPresentHandler : FormRuleHandler<PastOrPresent> {
    override fun annotation() = PastOrPresent::class.java

    override fun handle(field: Field, element: PastOrPresent): FormRule {
        val rule = FormRule()
        rule.type = "date"
        return rule
    }
}
