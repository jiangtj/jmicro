package com.jiangtj.micro.common.schema

import com.jiangtj.micro.common.JsonUtils.toJson
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class JsonSchemaGeneratorTest {

    data class BookForm(
        @field:NotBlank @field:Size(max = 128) val title: String,
        @field:NotBlank @field:Size(max = 64) val author: String,
        @field:Size(max = 32) val isbn: String? = null,
        @field:NotNull @field:DecimalMin("0") val price: BigDecimal,
        @field:NotNull @field:Min(0) val stock: Int,
    )

    data class BookShelf(
        val name: String,
        val books: MutableList<BookForm>? = null,
        val tags: Array<String>? = null,
        val createdAt: LocalDate,
        val updatedAt: LocalDateTime? = null,
        val status: Status? = null,
    ) {
        enum class Status {
            ENABLED,
            DISABLED
        }
    }

    @Test
    fun testGenerate() {
        val schema = JsonSchemaGenerator.generate<BookForm>()
        val json = toJson(schema)
        println(json)

        Assertions.assertEquals(JsonSchemaGenerator.DRAFT_2020_12, schema.schema)
        Assertions.assertEquals("object", schema.type)
        Assertions.assertEquals("BookForm", schema.title)
        Assertions.assertEquals(mutableListOf("title", "author", "price", "stock"), schema.required)
        Assertions.assertEquals(
            """{"type":"string","minLength":1,"maxLength":128}""",
            toJson(schema.properties!!["title"])
        )
        Assertions.assertEquals(
            """{"type":"string","maxLength":32}""",
            toJson(schema.properties!!["isbn"])
        )
        Assertions.assertEquals(
            """{"type":"number","minimum":0}""",
            toJson(schema.properties!!["price"])
        )
        Assertions.assertEquals(
            """{"type":"integer","minimum":0}""",
            toJson(schema.properties!!["stock"])
        )
    }

    @Test
    fun testNested() {
        val schema = JsonSchemaGenerator.generate<BookShelf>()
        val json = toJson(schema)
        println(json)

        val books = schema.properties!!["books"]!!
        Assertions.assertEquals("array", books.type)
        Assertions.assertEquals("object", books.items!!.type)
        Assertions.assertEquals("BookForm", books.items!!.title)

        val tags = schema.properties!!["tags"]!!
        Assertions.assertEquals("array", tags.type)
        Assertions.assertEquals("string", tags.items!!.type)

        Assertions.assertEquals("string", schema.properties!!["createdAt"]!!.type)
        Assertions.assertEquals("date", schema.properties!!["createdAt"]!!.format)
        Assertions.assertEquals("date-time", schema.properties!!["updatedAt"]!!.format)

        val status = schema.properties!!["status"]!!
        Assertions.assertEquals("string", status.type)
        Assertions.assertEquals(mutableListOf<Any>("ENABLED", "DISABLED"), status.enumValues)
    }

    data class Ids(
        val bookIds: List<Long>,
        val counts: List<Int>,
    )

    @Test
    fun testBoxedGenericItems() {
        // 泛型实参只会以装箱类型（java.lang.Long / java.lang.Integer）出现，不能被当成对象展开
        val schema = JsonSchemaGenerator.generate<Ids>()
        Assertions.assertEquals(
            """{"type":"array","items":{"type":"integer"}}""",
            toJson(schema.properties!!["bookIds"])
        )
        Assertions.assertEquals(
            """{"type":"array","items":{"type":"integer"}}""",
            toJson(schema.properties!!["counts"])
        )
    }

    @Test
    fun testCache() {
        JsonSchemaGenerator.generate<BookForm>()
        Assertions.assertTrue(JsonSchemaGenerator.cache.containsKey(BookForm::class.java))
    }
}
