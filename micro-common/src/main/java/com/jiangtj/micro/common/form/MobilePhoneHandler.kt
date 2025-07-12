package com.jiangtj.micro.common.form

import com.jiangtj.micro.common.validation.MobilePhone
import java.lang.reflect.Field

class MobilePhoneHandler : FormRuleHandler<MobilePhone> {
    override val annotation: Class<MobilePhone>
        get() = MobilePhone::class.java

    override fun handle(field: Field, element: MobilePhone): FormRule {
        val type = field.type
        if (type.isAssignableFrom(String::class.java)) {
            val rule = FormRule()
            rule.type = "string"
            rule.pattern = "^1[0-9]{10}$"
            rule.message = element.message
            return rule
        }
        if (type.isAssignableFrom(Long::class.java)) {
            val rule = FormRule()
            rule.type = "number"
            rule.min = 1000000000
            rule.max = 1999999999
            rule.message = element.message
            return rule
        }
        throw RuntimeException("不支持的规则类型")
    }
}
