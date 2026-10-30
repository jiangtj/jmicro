package __PKG__.book

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 图书实体（示例）。字段与 demo 的 `book` 表一致，接入数据库后可直接映射到生成的 POJO。
 */
data class Book(
    val id: Long?,
    val title: String,
    val author: String,
    val isbn: String?,
    val price: BigDecimal,
    val stock: Int,
    val createTime: LocalDateTime,
)
