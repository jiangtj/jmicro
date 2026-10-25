package __PKG__.book

import java.math.BigDecimal

/**
 * 图书实体（示例）。接入数据库后改为 jOOQ 生成的 POJO。
 */
data class Book(
    val id: Long?,
    val title: String,
    val author: String,
    val isbn: String?,
    val price: BigDecimal,
    val stock: Int,
)
