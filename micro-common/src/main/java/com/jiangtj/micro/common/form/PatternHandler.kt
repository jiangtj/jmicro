package com.jiangtj.micro.common.form

import jakarta.validation.constraints.Pattern
import java.lang.reflect.Field

/**
 * PatternHandler 类用于处理 `@Pattern` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Pattern` 类型的注解。
 */
class PatternHandler : FormRuleHandler<Pattern> {
    override fun annotation() = Pattern::class.java

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

