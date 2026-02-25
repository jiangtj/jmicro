package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import com.jiangtj.micro.common.utils.ListUtils.isList
import jakarta.validation.constraints.Size
import java.lang.reflect.Field

class SizeHandler : FormRuleHandler<Size> {
    override fun annotation() = Size::class.java

    override fun handle(field: Field, element: Size): FormRule {
        val rule = FormRule()
        val type = field.type
        if (CharSequence::class.java.isAssignableFrom(type)) {
            rule.type = "string"
        }
        if (isList(type)) {
            rule.type = "array"
        }
        if (element.min != 0) {
            rule.min = element.min
        }
        if (element.max != Int.MAX_VALUE) {
            rule.max = element.max
        }
        return rule
    }
}
