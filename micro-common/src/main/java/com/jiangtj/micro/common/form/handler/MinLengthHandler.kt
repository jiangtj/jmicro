package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import com.jiangtj.micro.common.validation.MinLength
import java.lang.reflect.Field

class MinLengthHandler : FormRuleHandler<MinLength> {
    override fun annotation() = MinLength::class.java

    override fun handle(field: Field, element: MinLength): FormRule {
        val rule = FormRule()
        rule.type = "string"
        rule.min = element.value
        return rule
    }
}
