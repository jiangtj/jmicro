package __PKG__.book

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

/**
 * 图书新增/修改的请求体。
 *
 * 校验注解同时用于 Bean Validation 与 JSON Schema 生成（`/books/form-schema`），
 * Kotlin 中必须用 `@field:` 目标，否则注解落在构造参数上不生效。
 */
data class BookForm(
    @field:NotBlank @field:Size(max = 128) val title: String,
    @field:NotBlank @field:Size(max = 64) val author: String,
    @field:Size(max = 32) val isbn: String? = null,
    @field:NotNull @field:DecimalMin("0") val price: BigDecimal,
    @field:NotNull @field:Min(0) val stock: Int,
)
