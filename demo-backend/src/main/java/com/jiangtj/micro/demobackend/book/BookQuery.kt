package com.jiangtj.micro.demobackend.book

import java.math.BigDecimal

/**
 * 图书分页查询参数。
 *
 * 属性名尽量与 jOOQ 生成的 `Book` pojo 保持一致，便于 `conditionByExample` 按属性名自动生成等值条件；
 * 不需要特殊处理的属性（[author]、[isbn]、[stock]）由 `conditionByExample` 直接生成等值条件；
 * 需要模糊或区间匹配的属性单独处理：
 * - [title]：模糊匹配（忽略大小写）
 * - [minPrice]、[maxPrice]：价格区间（闭区间）
 */
data class BookQuery(
    /** 书名，模糊匹配 */
    var title: String? = null,
    /** 作者，精确匹配 */
    var author: String? = null,
    /** ISBN，精确匹配 */
    var isbn: String? = null,
    /** 库存，精确匹配 */
    var stock: Int? = null,
    /** 价格下限（含） */
    var minPrice: BigDecimal? = null,
    /** 价格上限（含） */
    var maxPrice: BigDecimal? = null,
)
