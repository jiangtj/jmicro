package com.jiangtj.micro.common.form

import java.lang.reflect.Field

interface FormRuleHandler<A : Annotation> : BaseHandler {
    val annotation: Class<A>

    fun handle(field: Field, element: A): FormRule

    override fun handle(field: Field): FormRule? {
        return field.getAnnotation(this.annotation)
            ?.let { this.handle(field, it) }
    }
}
