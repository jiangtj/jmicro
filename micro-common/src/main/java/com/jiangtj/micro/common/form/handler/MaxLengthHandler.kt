package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import com.jiangtj.micro.common.validation.MaxLength
import java.lang.reflect.Field

class MaxLengthHandler : FormRuleHandler<MaxLength> {
    override fun annotation() = MaxLength::class.java

    override fun handle(field: Field, element: MaxLength): FormRule {
        val rule = FormRule()
        rule.type = "string"
        rule.max = element.value
        return rule
    }
}
