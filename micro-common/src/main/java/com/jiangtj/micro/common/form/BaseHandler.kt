package com.jiangtj.micro.common.form

import java.lang.reflect.Field

interface BaseHandler {
    fun handle(field: Field): FormRule?
}
