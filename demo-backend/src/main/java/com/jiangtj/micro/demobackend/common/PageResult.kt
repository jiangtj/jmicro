package com.jiangtj.micro.demobackend.common

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

/**
 * 统一分页响应体。
 *
 * 不直接返回 Spring Data 的 [Page]：其 JSON 结构不稳定，且携带 `pageable`、`sort` 等冗余字段，
 * 这里只暴露前端分页组件需要的字段。
 */
data class PageResult<T>(
    /** 当前页数据 */
    val content: List<T>,
    /** 当前页码，从 0 开始 */
    val page: Int,
    /** 每页条数 */
    val size: Int,
    /** 总记录数 */
    val totalElements: Long,
    /** 总页数 */
    val totalPages: Int,
    /** 是否第一页 */
    val first: Boolean,
    /** 是否最后一页 */
    val last: Boolean,
) {

    companion object {

        /**
         * 转换数据库分页结果
         */
        fun <T : Any> of(page: Page<T>): PageResult<T> = PageResult(
            content = page.content,
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            first = page.isFirst,
            last = page.isLast,
        )

        /**
         * 内存列表分页，用于系统配置项这类非数据库来源的数据
         */
        fun <T : Any> of(list: List<T>, pageable: Pageable): PageResult<T> {
            if (pageable.isUnpaged) {
                return of(PageImpl(list))
            }
            val from = pageable.offset.coerceAtMost(list.size.toLong()).toInt()
            val to = (from + pageable.pageSize).coerceAtMost(list.size)
            return of(PageImpl(list.subList(from, to), pageable, list.size.toLong()))
        }
    }
}
