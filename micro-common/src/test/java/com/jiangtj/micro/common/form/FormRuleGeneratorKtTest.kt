package com.jiangtj.micro.common.form

import com.jiangtj.micro.common.JsonUtils.toJson
import com.jiangtj.micro.common.form.FormRuleGenerator.cache
import com.jiangtj.micro.common.form.FormRuleGenerator.generate
import com.jiangtj.micro.common.validation.MaxLength
import com.jiangtj.micro.common.validation.MobilePhone
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import lombok.Data
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

private val log = KotlinLogging.logger {}

class FormRuleGeneratorKtTest {
    @Data
    data class Example(
        @field:MaxLength(5)
        val name: String? = null,
        @field:Min(18)
        val age: Int = 0,
        val address: String? = null,
        @field:MobilePhone
        @field:NotNull
        val phone: String? = null,
        @field:Pattern(regexp = "^1[0-9]{2}$")
        val patternVar: String? = null,
        @field:Valid
        val nest: NestDto? = null,
        @field:Size(min = 1)
        val arr1: Array<String>? = null,
        @field:Size(max = 2)
        val arr2: MutableList<String?>? = null,
        @field:Size(min = 3, max = 4)
        val char1: String? = null,
        @field:Email
        val mail: String? = null
    )

    @Data
    data class NestDto(
        @field:NotBlank
        val name: String? = null
    )

    @Test
    fun testGenerate() {
        val generate: MutableMap<String, MutableList<FormRule>> = generate<Example>()
        val json = toJson(generate)
        log.error { json }
        Assertions.assertTrue(json!!.contains("\"name\":[{\"type\":\"string\",\"max\":5}]"))
        Assertions.assertTrue(json.contains("\"age\":[{\"type\":\"number\",\"min\":18}]"))
        Assertions.assertFalse(json.contains("\"address\""))
        Assertions.assertTrue(json.contains("\"phone\":[{\"type\":\"string\",\"required\":true},{\"type\":\"string\",\"pattern\":\"^1[0-9]{10}$\",\"message\":\"手机号格式不正确\"}]"))
        Assertions.assertTrue(json.contains("\"patternVar\":[{\"type\":\"string\",\"pattern\":\"^1[0-9]{2}$\",\"message\":\"需要匹配正则表达式: ^1[0-9]{2}$\"}]"))
        Assertions.assertTrue(json.contains("\"nest\":[{\"type\":\"object\",\"defaultField\":{\"name\":[{\"type\":\"string\",\"required\":true,\"whitespace\":true}]}}]"))
        Assertions.assertTrue(json.contains("\"arr1\":[{\"type\":\"array\",\"min\":1}]"))
        Assertions.assertTrue(json.contains("\"arr2\":[{\"type\":\"array\",\"max\":2}]"))
        Assertions.assertTrue(json.contains("\"char1\":[{\"type\":\"string\",\"min\":3,\"max\":4}]"))
        Assertions.assertTrue(json.contains("\"mail\":[{\"type\":\"email\"}]"))
    }

    @Test
    fun testCache() {
        generate<Example>()
        val cache: MutableMap<String, MutableMap<String, MutableList<FormRule>>> = cache
        Assertions.assertTrue(cache.containsKey(Example::class.java.getName()))
    }

    @Data
    data class ExampleForArr(
        @field:NotEmpty
        val mail: MutableList<String>? = null,
        @field:Valid
        val nestLS: MutableList<NestL>? = null
    )

    @Data
    data class NestL(
        @field:NotBlank
        val name: String? = null
    )

    @Test
    fun testListV() {
        val generate: MutableMap<String, MutableList<FormRule>> = generate<ExampleForArr>()
        val json = toJson(generate)
        log.error { json }
        Assertions.assertEquals(
            "{" +
                    "\"mail\":[{\"type\":\"array\",\"required\":true}]," +
                    "\"nestLS\":[{\"type\":\"array\",\"defaultField\":{\"name\":[{\"type\":\"string\",\"required\":true,\"whitespace\":true}]}}]" +
                    "}", json
        )
    }
}