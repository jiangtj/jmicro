package com.jiangtj.micro.common.schema

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.jiangtj.micro.common.validation.MaxLength
import com.jiangtj.micro.common.validation.MinLength
import com.jiangtj.micro.common.validation.MobilePhone
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Negative
import jakarta.validation.constraints.NegativeOrZero
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import tools.jackson.databind.JavaType
import tools.jackson.module.kotlin.isKotlinClass
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.jvm.kotlinProperty

/**
 * JSON Schema 生成器，根据数据类的字段类型、Jackson 注解与 `jakarta.validation` 注解
 * 生成 [JSON Schema](https://json-schema.org/)（默认 draft 2020-12）。
 *
 * 支持的能力：
 * - 基础类型映射（字符串、数字、布尔、日期时间、`UUID`、枚举等）
 * - 嵌套对象、集合、数组、`Map` 的递归展开
 * - Kotlin 可空属性 / `@NotNull` / `@NotBlank` / `@NotEmpty` 转换为 `required`
 * - `@Size`、`@Min`、`@Max`、`@DecimalMin`、`@DecimalMax`、`@Digits`、`@Pattern`、
 *   `@Email`、`@MobilePhone`、`@MinLength`、`@MaxLength` 等约束转换为对应关键字
 * - `@JsonProperty` 重命名、`@JsonIgnore` 忽略、`@Deprecated` 标记
 *
 * 生成结果按类缓存，重复调用不会重复解析。
 */
object JsonSchemaGenerator {

    /** JSON Schema draft 2020-12 的 `$schema` 值 */
    const val DRAFT_2020_12 = "https://json-schema.org/draft/2020-12/schema"

    private const val MOBILE_PHONE_PATTERN = "^1[0-9]{10}$"

    /**
     * `org.springframework.util.unit.DataSize` 的全限定名。
     *
     * 这里按类名匹配而不是直接引用，避免 `micro-common` 对 spring-core（`compileOnly`）产生运行时依赖。
     */
    private const val DATA_SIZE_CLASS_NAME = "org.springframework.util.unit.DataSize"

    val cache: MutableMap<Class<*>, JsonSchema> = ConcurrentHashMap()

    /**
     * 泛型类型的 schema 缓存，key 为 [Type]（`JavaType` 实现了该接口）
     */
    private val typeCache: MutableMap<Type, JsonSchema> = ConcurrentHashMap()

    /**
     * 基于泛型类型生成 JSON Schema 的便捷方法。
     */
    inline fun <reified T> generate(): JsonSchema = generate(T::class.java)

    /**
     * 根据给定类型生成 JSON Schema，结果会被缓存。
     *
     * @param clazz 目标类型。
     * @return 带有 `$schema` 与 `title` 的 JSON Schema。
     */
    @JvmStatic
    fun generate(clazz: Class<*>): JsonSchema = cache.getOrPut(clazz) {
        val schema = schemaOf(clazz, mutableSetOf())
        schema.schema = DRAFT_2020_12
        if (schema.title == null) {
            schema.title = clazz.simpleName
        }
        schema
    }

    /**
     * 根据任意泛型类型生成 JSON Schema，支持 `Map<String, X>`、`List<X>` 等嵌套结构。
     *
     * 与 [generate] 相同，结果会被缓存。
     *
     * @param type 目标类型，通常来自 `ObjectMapper.typeFactory.constructType(...)`
     *   或 `TypeReference.type`。
     */
    @JvmStatic
    fun generate(type: Type): JsonSchema = typeCache.getOrPut(type) {
        val schema = schemaOf(type, mutableSetOf())
        schema.schema = DRAFT_2020_12
        if (schema.title == null && type is Class<*>) {
            schema.title = type.simpleName
        }
        schema
    }

    private fun schemaOf(clazz: Class<*>, visiting: MutableSet<Class<*>>): JsonSchema {
        primitiveSchema(clazz)?.let { return it }

        if (clazz.isEnum) {
            return JsonSchema(
                type = "string",
                enumValues = clazz.enumConstants.map { (it as Enum<*>).name }.toMutableList()
            )
        }

        if (clazz.isArray) {
            val componentType = clazz.componentType
            if (componentType == Byte::class.javaPrimitiveType) {
                return JsonSchema(type = "string", format = "byte")
            }
            return JsonSchema(type = "array", items = schemaOf(componentType, visiting))
        }

        if (Collection::class.java.isAssignableFrom(clazz)) {
            return JsonSchema(type = "array")
        }

        if (Map::class.java.isAssignableFrom(clazz)) {
            return JsonSchema(type = "object")
        }

        if (clazz == Any::class.java) {
            return JsonSchema()
        }

        return objectSchema(clazz, visiting)
    }

    private fun schemaOf(javaType: Type, visiting: MutableSet<Class<*>>): JsonSchema {
        // Jackson 的 JavaType 不实现 ParameterizedType，需要单独展开
        if (javaType is JavaType) {
            return schemaOfJavaType(javaType, visiting)
        }
        if (javaType is ParameterizedType) {
            val rawType = javaType.rawType as? Class<*> ?: return JsonSchema()
            if (Collection::class.java.isAssignableFrom(rawType)) {
                val itemType = javaType.actualTypeArguments.firstOrNull() ?: Any::class.java
                return JsonSchema(type = "array", items = schemaOf(unwrap(itemType), visiting))
            }
            if (Map::class.java.isAssignableFrom(rawType)) {
                val valueType = javaType.actualTypeArguments.getOrNull(1) ?: Any::class.java
                return JsonSchema(type = "object", additionalProperties = schemaOf(unwrap(valueType), visiting))
            }
            return schemaOf(rawType, visiting)
        }
        val clazz = javaType as? Class<*> ?: return JsonSchema()
        return schemaOf(clazz, visiting)
    }

    private fun schemaOfJavaType(javaType: JavaType, visiting: MutableSet<Class<*>>): JsonSchema {
        if (javaType.isCollectionLikeType || javaType.isArrayType) {
            val itemType = javaType.contentType ?: return JsonSchema(type = "array")
            return JsonSchema(type = "array", items = schemaOfJavaType(itemType, visiting))
        }
        if (javaType.isMapLikeType) {
            val valueType = javaType.contentType ?: return JsonSchema(type = "object")
            return JsonSchema(type = "object", additionalProperties = schemaOfJavaType(valueType, visiting))
        }
        return schemaOf(javaType.rawClass, visiting)
    }

    private fun unwrap(javaType: Type): Type =
        if (javaType is WildcardType) javaType.upperBounds.firstOrNull() ?: Any::class.java else javaType

    private fun primitiveSchema(clazz: Class<*>): JsonSchema? = when {
        clazz == String::class.java || clazz == Char::class.java || CharSequence::class.java.isAssignableFrom(clazz) ->
            JsonSchema(type = "string")

        clazz == Boolean::class.javaPrimitiveType || clazz == Boolean::class.java -> JsonSchema(type = "boolean")

        clazz == Int::class.javaPrimitiveType || clazz == Int::class.java
            || clazz == Short::class.javaPrimitiveType || clazz == Short::class.java
            || clazz == Byte::class.javaPrimitiveType || clazz == Byte::class.java
            || clazz == Long::class.javaPrimitiveType || clazz == Long::class.java
            || clazz == BigInteger::class.java -> JsonSchema(type = "integer")

        clazz == Float::class.javaPrimitiveType || clazz == Float::class.java
            || clazz == Double::class.javaPrimitiveType || clazz == Double::class.java
            || clazz == BigDecimal::class.java -> JsonSchema(type = "number")

        clazz == LocalDate::class.java -> JsonSchema(type = "string", format = "date")
        clazz == LocalTime::class.java -> JsonSchema(type = "string", format = "time")
        clazz == LocalDateTime::class.java || clazz == OffsetDateTime::class.java
            || clazz == ZonedDateTime::class.java || clazz == Instant::class.java
            || clazz == Date::class.java -> JsonSchema(type = "string", format = "date-time")

        clazz == UUID::class.java -> JsonSchema(type = "string", format = "uuid")
        clazz == URI::class.java || clazz == URL::class.java -> JsonSchema(type = "string", format = "uri")

        // Spring 的 DataSize（如 `10MB`）在配置中以字符串表示，内部字段不参与序列化
        clazz.name == DATA_SIZE_CLASS_NAME -> JsonSchema(type = "string")

        else -> null
    }

    private fun objectSchema(clazz: Class<*>, visiting: MutableSet<Class<*>>): JsonSchema {
        // 循环引用时只返回类型，避免无限递归
        if (!visiting.add(clazz)) {
            return JsonSchema(type = "object")
        }

        val properties = LinkedHashMap<String, JsonSchema>()
        val required = mutableListOf<String>()
        val isKotlin = clazz.isKotlinClass()

        try {
            for (field in clazz.declaredFields) {
                if (field.isSynthetic
                    || Modifier.isStatic(field.modifiers)
                    || Modifier.isTransient(field.modifiers)
                    || field.isAnnotationPresent(JsonIgnore::class.java)
                ) {
                    continue
                }

                val name = propertyName(field)
                val fieldSchema = schemaOf(field.genericType, visiting)
                applyConstraints(field, fieldSchema)

                properties[name] = fieldSchema
                if (isRequired(field, isKotlin)) {
                    required.add(name)
                }
            }
        } finally {
            visiting.remove(clazz)
        }

        return JsonSchema(
            title = clazz.simpleName,
            type = "object",
            properties = properties,
            required = required.ifEmpty { null },
        )
    }

    private fun propertyName(field: Field): String =
        field.getAnnotation(JsonProperty::class.java)?.value?.takeIf { it.isNotEmpty() } ?: field.name

    private fun isRequired(field: Field, isKotlin: Boolean): Boolean {
        if (field.type.isPrimitive) {
            return true
        }
        if (field.isAnnotationPresent(NotNull::class.java)
            || field.isAnnotationPresent(NotBlank::class.java)
            || field.isAnnotationPresent(NotEmpty::class.java)
        ) {
            return true
        }
        if (field.getAnnotation(JsonProperty::class.java)?.required == true) {
            return true
        }
        if (!isKotlin) {
            return false
        }
        val property = runCatching { field.kotlinProperty }.getOrNull() ?: return false
        return !property.returnType.isMarkedNullable
    }

    private fun applyConstraints(field: Field, schema: JsonSchema) {
        val isCollection = schema.type == "array"

        field.getAnnotation(NotBlank::class.java)?.let {
            schema.type = "string"
            schema.minLength = schema.minLength ?: 1
        }

        field.getAnnotation(NotEmpty::class.java)?.let {
            if (isCollection) {
                schema.minItems = schema.minItems ?: 1
            } else {
                schema.type = "string"
                schema.minLength = schema.minLength ?: 1
            }
        }

        field.getAnnotation(Size::class.java)?.let { size ->
            if (isCollection) {
                if (size.min > 0) schema.minItems = size.min
                if (size.max < Int.MAX_VALUE) schema.maxItems = size.max
            } else if (schema.type == "string") {
                if (size.min > 0) schema.minLength = size.min
                if (size.max < Int.MAX_VALUE) schema.maxLength = size.max
            }
        }

        field.getAnnotation(MinLength::class.java)?.let {
            schema.minLength = it.value
        }

        field.getAnnotation(MaxLength::class.java)?.let {
            schema.maxLength = it.value
        }

        field.getAnnotation(Min::class.java)?.let {
            schema.minimum = it.value
        }

        field.getAnnotation(Max::class.java)?.let {
            schema.maximum = it.value
        }

        field.getAnnotation(DecimalMin::class.java)?.let {
            if (it.inclusive) schema.minimum = BigDecimal(it.value) else schema.exclusiveMinimum = BigDecimal(it.value)
        }

        field.getAnnotation(DecimalMax::class.java)?.let {
            if (it.inclusive) schema.maximum = BigDecimal(it.value) else schema.exclusiveMaximum = BigDecimal(it.value)
        }

        field.getAnnotation(Positive::class.java)?.let {
            schema.exclusiveMinimum = 0
        }

        field.getAnnotation(PositiveOrZero::class.java)?.let {
            schema.minimum = 0
        }

        field.getAnnotation(Negative::class.java)?.let {
            schema.exclusiveMaximum = 0
        }

        field.getAnnotation(NegativeOrZero::class.java)?.let {
            schema.maximum = 0
        }

        field.getAnnotation(Digits::class.java)?.let { digits ->
            if (digits.fraction > 0) {
                schema.multipleOf = BigDecimal.ONE.movePointLeft(digits.fraction)
            }
            if (digits.integer in 1..18) {
                schema.maximum = BigDecimal.TEN.pow(digits.integer).subtract(BigDecimal.ONE.movePointLeft(digits.fraction))
            }
        }

        field.getAnnotation(Pattern::class.java)?.let {
            schema.type = "string"
            schema.pattern = it.regexp
        }

        field.getAnnotation(Email::class.java)?.let {
            schema.type = "string"
            schema.format = "email"
        }

        field.getAnnotation(MobilePhone::class.java)?.let {
            schema.type = "string"
            schema.pattern = MOBILE_PHONE_PATTERN
        }

        if (field.isAnnotationPresent(Deprecated::class.java)) {
            schema.deprecated = true
        }
    }
}
