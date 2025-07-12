package com.jiangtj.micro.common.form

import jakarta.validation.constraints.Pattern
import java.lang.reflect.Field

class PatternHandler : FormRuleHandler<Pattern> {
    override val annotation: Class<Pattern>
        get() = Pattern::class.java

    override fun handle(field: Field, element: Pattern): FormRule {
        var message = element.message
        if ("{jakarta.validation.constraints.Pattern.message}" == message) {
            message = "需要匹配正则表达式: " + element.regexp
        }
        val rule = FormRule()
        rule.type = "string"
        rule.pattern = element.regexp
        rule.message = message
        return rule
    }
}
