package com.jiangtj.micro.common.form.handler

import com.jiangtj.micro.common.form.FormRule
import com.jiangtj.micro.common.form.FormRuleHandler
import com.jiangtj.micro.common.utils.ListUtils.isList
import jakarta.validation.constraints.Size
import java.lang.reflect.Field

/**
 * SizeHandler 类用于处理 `@Size` 注解，将其转换为表单验证规则。
 * 实现了 `FormRuleHandler` 接口，专门处理 `Size` 类型的注解。
 * 根据字段类型生成 string 或 array 类型的规则，并设置相应的 min 和 max 属性。
 */
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
