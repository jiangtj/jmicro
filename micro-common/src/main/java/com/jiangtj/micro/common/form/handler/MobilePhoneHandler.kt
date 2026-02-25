package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import com.jiangtj.micro.common.validation.MobilePhone
import java.lang.reflect.Field

/**
 * MobilePhoneHandler 类实现了 FormRuleHandler 接口，用于处理带有 MobilePhone 注解的字段，
 * 根据字段类型生成相应的手机格式验证规则。
 */
class MobilePhoneHandler : FormRuleHandler<MobilePhone> {
    override fun annotation() = MobilePhone::class.java

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