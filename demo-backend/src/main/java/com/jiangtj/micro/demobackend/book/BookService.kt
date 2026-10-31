package com.jiangtj.micro.demobackend.book

import com.jiangtj.micro.demobackend.common.withDefaultSort
import com.jiangtj.micro.demobackend.jooq.tables.Book as BookTable
import com.jiangtj.micro.demobackend.jooq.tables.daos.BookDao
import com.jiangtj.micro.demobackend.jooq.tables.pojos.Book
import com.jiangtj.micro.sql.jooq.SortPropertyStyle
import com.jiangtj.micro.sql.jooq.dao.kt.fetchPage
import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 图书增删改查，持久层使用 jOOQ 生成的 BookDao
 */
@Service
class BookService(
    private val bookDao: BookDao,
) {

    fun list(): List<Book> = bookDao.findAll()

    /**
     * 图书分页查询。
     *
     * 排序字段按 pojo 属性名（驼峰）传入，如 `sortQ=createTime,desc`，未指定时按主键倒序（新书在前）。
     * [keyword] 同时匹配书名与作者（忽略大小写）。
     */
    fun page(pageable: Pageable, keyword: String? = null): Page<Book> {
        val kw = keyword?.trim()
        val condition = if (kw.isNullOrEmpty()) {
            null
        } else {
            BookTable.BOOK.TITLE.containsIgnoreCase(kw)
                .or(BookTable.BOOK.AUTHOR.containsIgnoreCase(kw))
        }
        return bookDao.fetchPage()
            .where(condition)
            .pageable(
                pageable.withDefaultSort(Sort.by(Sort.Direction.DESC, "id")),
                SortPropertyStyle.SNAKE_LOWER,
            )
            .fetchPage()
    }

    fun get(id: Long): Book = bookDao.fetchOneById(id)
        ?: throw BaseExceptionUtils.notFound("图书不存在：$id")

    fun create(form: BookForm): Book {
        val book = Book(
            id = null,
            title = form.title,
            author = form.author,
            isbn = form.isbn,
            price = form.price,
            stock = form.stock,
            createTime = LocalDateTime.now(),
        )
        bookDao.insert(book)
        return book
    }

    fun update(id: Long, form: BookForm): Book {
        val book = get(id)
        book.title = form.title
        book.author = form.author
        book.isbn = form.isbn
        book.price = form.price
        book.stock = form.stock
        bookDao.update(book)
        return book
    }

    fun delete(id: Long) {
        // 先确认存在，不存在时返回 404
        get(id)
        bookDao.deleteById(id)
    }
}
