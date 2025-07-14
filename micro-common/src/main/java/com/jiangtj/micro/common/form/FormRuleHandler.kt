package com.jiangtj.micro.common.form

import java.lang.reflect.Field

interface FormRuleHandler<A : Annotation> : BaseHandler {
    /**
     * 获取当前处理器所处理的注解类型的 `Class` 对象。
     * 该方法返回一个 `Class` 对象，代表当前处理器能够处理的注解类型。
     * 在处理反射字段时，会使用该注解类型来查找字段上的对应注解。
     *
     * @return 当前处理器所处理的注解类型的 `Class` 对象。
     */
    fun annotation(): Class<A>

    /**
     * 处理带有指定注解的反射字段，生成对应的表单规则。
     *
     * 该方法接收一个反射字段和该字段上的注解实例，根据注解的信息和字段的属性，
     * 生成并返回与之匹配的表单规则对象。实现此方法的类需要提供具体的规则生成逻辑。
     *
     * @param field  需要处理的反射字段对象。
     * @param element 该字段上的注解实例，类型为 [A]。
     * @return 与该字段和注解对应的表单规则对象 [FormRule]。
     */
    fun handle(field: Field, element: A): FormRule

    override fun handle(field: Field): FormRule? {
        return field.getAnnotation(this.annotation())
            ?.let { this.handle(field, it) }
    }
}
