package com.jiangtj.micro.demobackend.book;

import com.jiangtj.micro.demobackend.jooq.tables.daos.BookDao;
import com.jiangtj.micro.demobackend.jooq.tables.pojos.Book;
import com.jiangtj.micro.web.BaseExceptionUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图书增删改查，持久层使用 jOOQ 生成的 BookDao
 */
@Service
public class BookService {

    @Resource
    private BookDao bookDao;

    public List<Book> list() {
        return bookDao.findAll();
    }

    public Book get(Long id) {
        Book book = bookDao.fetchOneById(id);
        if (book == null) {
            throw BaseExceptionUtils.notFound("图书不存在：" + id);
        }
        return book;
    }

    public Book create(BookForm form) {
        Book book = new Book(null, form.title(), form.author(), form.isbn(),
            form.price(), form.stock(), LocalDateTime.now());
        bookDao.insert(book);
        return book;
    }

    public Book update(Long id, BookForm form) {
        Book book = get(id);
        book.setTitle(form.title());
        book.setAuthor(form.author());
        book.setIsbn(form.isbn());
        book.setPrice(form.price());
        book.setStock(form.stock());
        bookDao.update(book);
        return book;
    }

    public void delete(Long id) {
        // 先确认存在，不存在时返回 404
        get(id);
        bookDao.deleteById(id);
    }
}
