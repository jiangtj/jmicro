package com.jiangtj.micro.demobackend.book

import com.jiangtj.micro.demobackend.jooq.tables.daos.BookDao
import com.jiangtj.micro.demobackend.jooq.tables.pojos.Book
import com.jiangtj.micro.web.BaseExceptionUtils
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
