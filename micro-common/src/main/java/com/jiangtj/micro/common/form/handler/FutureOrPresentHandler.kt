package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.FutureOrPresent
import java.lang.reflect.Field

class FutureOrPresentHandler : FormRuleHandler<FutureOrPresent> {
    override fun annotation() = FutureOrPresent::class.java

    override fun handle(field: Field, element: FutureOrPresent): FormRule {
        val rule = FormRule()
        rule.type = "date"
        return rule
    }
}
