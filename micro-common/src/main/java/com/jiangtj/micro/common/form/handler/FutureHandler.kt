package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Future
import java.lang.reflect.Field

class FutureHandler : FormRuleHandler<Future> {
    override fun annotation() = Future::class.java

    override fun handle(field: Field, element: Future): FormRule {
        val rule = FormRule()
        rule.type = "date"
        return rule
    }
}
