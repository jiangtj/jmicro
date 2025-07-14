package com.jiangtj.micro.common.form

import java.lang.reflect.Field

/**
 * 定义一个基础处理器接口，用于处理反射字段并生成表单规则。
 * 实现该接口的类需要提供具体的逻辑，将反射字段转换为对应的表单规则对象。
 */
interface BaseHandler {

    /**
     * 处理给定的反射字段，生成对应的表单规则。
     *
     * 该方法会对传入的反射字段进行分析，根据字段的属性、注解等信息生成相应的表单规则。
     * 如果该字段不需要生成表单规则，则返回 `null`。
     *
     * @param field 需要处理的反射字段对象。
     * @return 与该字段对应的表单规则对象 [FormRule]，若不需要生成规则则返回 `null`。
     */
    fun handle(field: Field): FormRule?

}
