package com.jiangtj.micro.common.form

import com.jiangtj.micro.common.validation.MaxLength
import com.jiangtj.micro.common.validation.MinLength
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

object FormRuleGenerator {

    val cache: MutableMap<String, MutableMap<String, MutableList<FormRule>>> = mutableMapOf()
    val handlers: MutableList<BaseHandler> = mutableListOf()

    init {
        addHandler(PatternHandler())
        addHandler(MobilePhoneHandler())
    }

    @JvmStatic
    fun generate(clazz: Class<*>): MutableMap<String, MutableList<FormRule>> {
        var map = cache[clazz.getName()]
        if (map != null) {
            return map
        }

        map = mutableMapOf()

        val fields = clazz.getDeclaredFields()
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

    private fun isList(type: Class<*>): Boolean {
        return type.isArray || MutableCollection::class.java.isAssignableFrom(type)
    }

    private fun handle(field: Field, rules: MutableList<FormRule>) {
        for (handler in handlers) {
            val rule = handler.handle(field)
            if (rule != null) {
                rules.add(rule)
            }
        }
    }

    @JvmStatic
    fun addHandler(handler: BaseHandler) {
        handlers.add(handler)
    }
}
