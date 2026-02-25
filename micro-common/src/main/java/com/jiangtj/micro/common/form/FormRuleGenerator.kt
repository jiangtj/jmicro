package com.jiangtj.micro.common.form

import com.jiangtj.micro.common.form.handler.MobilePhoneHandler
import com.jiangtj.micro.common.form.handler.PatternHandler
import com.jiangtj.micro.common.utils.ListUtils.isList
import com.jiangtj.micro.common.validation.MaxLength
import com.jiangtj.micro.common.validation.MinLength
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import tools.jackson.module.kotlin.isKotlinClass
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import kotlin.reflect.jvm.kotlinProperty

/**
 * 表单规则生成器，用于根据类的字段注解自动生成表单验证规则。
 * 该类使用单例模式，包含缓存机制以避免重复生成相同类的规则。
 */
object FormRuleGenerator {

    val cache: MutableMap<String, MutableMap<String, MutableList<FormRule>>> = mutableMapOf()
    val handlers: MutableList<BaseHandler> = mutableListOf()

    /**
     * 初始化块，在对象创建时执行。
     * 向处理器列表中添加默认的处理器。
     */
    init {
        addHandler(PatternHandler())
        addHandler(MobilePhoneHandler())
    }

    /**
     * 基于泛型类型自动生成表单规则的便捷方法。
     * 该方法是 `generate(Class<*>)` 方法的泛型版本，
     * 利用 Kotlin 的内联函数和具体化类型参数特性，
     * 允许直接通过泛型类型参数指定要生成表单规则的类，
     * 避免手动传递 Class 对象。
     *
     * @param T 要生成表单规则的类的泛型类型。
     * @return 一个可变映射，键为字段名，值为对应字段的表单规则列表。
     */
    inline fun <reified T> generate() = generate(T::class.java)

    /**
     * 根据给定的类生成表单验证规则。
     * 首先检查缓存中是否已存在该类的规则，如果存在则直接返回，
     * 否则遍历类的所有字段，根据字段类型和注解生成规则，并将结果存入缓存。
     *
     * @param clazz 要生成表单规则的类。
     * @return 一个可变映射，键为字段名，值为对应字段的表单规则列表。
     */
    @JvmStatic
    fun generate(clazz: Class<*>): MutableMap<String, MutableList<FormRule>> {
        var map = cache[clazz.getName()]
        if (map != null) {
            return map
        }

        map = mutableMapOf()

        val fields = clazz.getDeclaredFields()
        val isKt = clazz.isKotlinClass()
        for (field in fields) {
            val rules: MutableList<FormRule> = mutableListOf()
            var setField = false
            val rule = FormRule()

            val type = field.type
            if (Int::class.java.isAssignableFrom(type)
                || Int::class.javaPrimitiveType!!.isAssignableFrom(type)
                || Byte::class.java.isAssignableFrom(type)
                || Byte::class.javaPrimitiveType!!.isAssignableFrom(type)
                || Long::class.java.isAssignableFrom(type)
                || Long::class.javaPrimitiveType!!.isAssignableFrom(type)
            ) {
                rule.type = "number"
                setField = true
            }
            if (Boolean::class.java.isAssignableFrom(type)
                || Boolean::class.javaPrimitiveType!!.isAssignableFrom(type)
            ) {
                rule.type = "boolean"
                setField = true
            }

            field.getAnnotation(NotNull::class.java)?.let {
                rule.required = true
                setField = true
            }

            field.getAnnotation(NotEmpty::class.java)?.let {
                rule.type = if (isList(type)) "array" else "string"
                rule.required = true
                setField = true
            }

            field.getAnnotation(NotBlank::class.java)?.let {
                rule.type = "string"
                rule.required = true
                rule.whitespace = true
                setField = true
            }

            field.getAnnotation(Min::class.java)?.let {
                rule.type = "number"
                rule.min = it.value.toInt()
                setField = true
            }

            field.getAnnotation(Max::class.java)?.let {
                rule.type = "number"
                rule.max = it.value.toInt()
                setField = true
            }

            field.getAnnotation(Size::class.java)?.let {
                if (type.isAssignableFrom(CharSequence::class.java)) {
                    rule.type = "string"
                }
                if (isList(type)) {
                    rule.type = "array"
                }
                if (it.min != 0) {
                    rule.min = it.min
                }
                if (it.max != Int.MAX_VALUE) {
                    rule.max = it.max
                }
                setField = true
            }

            field.getAnnotation(Email::class.java)?.let {
                rule.type = "email"
                setField = true
            }

            field.getAnnotation(Positive::class.java)?.let {
                rule.type = "number"
                rule.min = 1
                setField = true
            }

            field.getAnnotation(PositiveOrZero::class.java)?.let {
                rule.type = "number"
                rule.min = 0
                setField = true
            }

            field.getAnnotation(Negative::class.java)?.let {
                rule.type = "number"
                rule.max = -1
                setField = true
            }

            field.getAnnotation(NegativeOrZero::class.java)?.let {
                rule.type = "number"
                rule.max = 0
                setField = true
            }

            field.getAnnotation(DecimalMin::class.java)?.let {
                rule.type = "number"
                rule.min = it.value.toDouble().toInt()
                setField = true
            }

            field.getAnnotation(DecimalMax::class.java)?.let {
                rule.type = "number"
                rule.max = it.value.toDouble().toInt()
                setField = true
            }

            field.getAnnotation(Digits::class.java)?.let {
                rule.type = "number"
                setField = true
            }

            field.getAnnotation(Past::class.java)?.let {
                rule.type = "date"
                setField = true
            }

            field.getAnnotation(PastOrPresent::class.java)?.let {
                rule.type = "date"
                setField = true
            }

            field.getAnnotation(Future::class.java)?.let {
                rule.type = "date"
                setField = true
            }

            field.getAnnotation(FutureOrPresent::class.java)?.let {
                rule.type = "date"
                setField = true
            }

            field.getAnnotation(MinLength::class.java)?.let {
                rule.type = "string"
                rule.min = it.value
                setField = true
            }

            field.getAnnotation(MaxLength::class.java)?.let {
                rule.type = "string"
                rule.max = it.value
                setField = true
            }

            if (isKt) {
                val kProperty = field.kotlinProperty
                if (kProperty != null) {
                    if (!kProperty.returnType.isMarkedNullable) {
                        rule.required = true
                        setField = true
                    }
                }
            }

            if (setField) {
                if (rule.type == null && type.isAssignableFrom(String::class.java)) {
                    rule.type = "string"
                }
                rules.add(rule)
            }

            handle(field, rules)

            field.getAnnotation(Valid::class.java)?.let {
                val validRule = FormRule()
                if (isList(type)) {
                    validRule.type = "array"
                    val genericType = field.genericType
                    if (genericType is ParameterizedType) {
                        val actualType = genericType.actualTypeArguments[0]
                        validRule.defaultField = generate(actualType as Class<*>)
                    }
                } else {
                    validRule.type = "object"
                    validRule.defaultField = generate(field.getType())
                }
                rules.add(validRule)
            }

            if (!rules.isEmpty()) {
                map[field.name] = rules
            }
        }

        cache[clazz.getName()] = map
        return map
    }

    private fun handle(field: Field, rules: MutableList<FormRule>) {
        for (handler in handlers) {
            val rule = handler.handle(field)
            if (rule != null) {
                rules.add(rule)
            }
        }
    }

    /**
     * 向处理器列表中添加一个新的处理器。
     * 该处理器将在生成表单规则时用于处理特定的字段注解。
     * 添加的处理器会在字段遍历过程中依次被调用，以生成额外的表单规则。
     *
     * @param handler 要添加的处理器实例，类型为 [BaseHandler]。
     */
    @JvmStatic
    fun addHandler(handler: BaseHandler) {
        handlers.add(handler)
    }
}
