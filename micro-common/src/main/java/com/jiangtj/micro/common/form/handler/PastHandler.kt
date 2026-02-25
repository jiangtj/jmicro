package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Past
import java.lang.reflect.Field

class PastHandler : FormRuleHandler<Past> {
    override fun annotation() = Past::class.java

    override fun handle(field: Field, element: Past): FormRule {
        val rule = FormRule()
        rule.type = "date"
        return rule
    }
}
