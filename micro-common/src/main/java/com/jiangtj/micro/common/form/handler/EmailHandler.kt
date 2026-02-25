package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import jakarta.validation.constraints.Email
import java.lang.reflect.Field

class EmailHandler : FormRuleHandler<Email> {
    override fun annotation() = Email::class.java

    override fun handle(field: Field, element: Email): FormRule {
        val rule = FormRule()
        rule.type = "email"
        return rule
    }
}
