package __PKG__.common

import com.jiangtj.micro.sql.jooq.SortPropertyStyle
import com.jiangtj.micro.sql.jooq.dao.kt.PageableStep
import org.jooq.Record
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * 将 Spring Data 的 `Pageable` 转换为 jOOQ 的 `PageableStep`（需要 `micro-sql-jooq`）。
 *
 * 排序属性用 pojo 的驼峰名传入（`sortQ=createTime,desc`），这里转成下划线列名；
 * 未指定排序时按主键倒序，保证翻页稳定。
 *
 * 典型用法（demo 的 `BookService.page`）：
 * ```kotlin
 * bookDao.fetchPage()
 *     .conditionByExample(query, table.TITLE, table.PRICE)
 *     .and(query.title?.trim()?.takeIf { it.isNotEmpty() }?.let { table.TITLE.containsIgnoreCase(it) })
 *     .withPageable(pageable)
 *     .fetchPage()
 * ```
 */
fun <T : Record> PageableStep<T>.withPageable(pageable: Pageable) =
    this.pageable(
        pageable.withDefaultSort(Sort.by(Sort.Direction.DESC, "id")),
        SortPropertyStyle.SNAKE_LOWER,
    )
