package __PKG__.book

import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * 图书增删改查（内存实现，便于项目零依赖启动）。
 *
 * 接入数据库后：引入 Flyway + jOOQ，用生成的 `BookDao` 替换本类实现，
 * 分页用 `PageUtils` 或 `KtDao.fetchPage`，业务层接口保持不变。
 */
@Service
class BookService {

    private val books = ConcurrentHashMap<Long, Book>()
    private val sequence = AtomicLong(0)

    fun list(): List<Book> = books.values.sortedBy { it.id }

    fun get(id: Long): Book = books[id] ?: throw BaseExceptionUtils.notFound("图书不存在：$id")

    fun create(form: BookForm): Book {
        val id = sequence.incrementAndGet()
        val book = form.toBook(id)
        books[id] = book
        return book
    }

    fun update(id: Long, form: BookForm): Book {
        get(id)
        val book = form.toBook(id)
        books[id] = book
        return book
    }

    fun delete(id: Long) {
        // 先确认存在，不存在时返回 404
        get(id)
        books.remove(id)
    }

    private fun BookForm.toBook(id: Long): Book = Book(
        id = id,
        title = title,
        author = author,
        isbn = isbn,
        price = price,
        stock = stock,
    )
}
