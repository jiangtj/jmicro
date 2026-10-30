package __PKG__.book

import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * 图书增删改查（内存实现，便于项目零依赖启动）。
 *
 * 接入数据库后（`--features db`）：引入 Flyway + jOOQ，用生成的 `BookDao` 替换本类实现
 * （见 `references/database.md` 的 demo 版本），业务层方法签名保持不变；
 * 分页用 `PageUtils` 或 `KtDao.fetchPage`。
 */
@Service
class BookService {

    private val books = ConcurrentHashMap<Long, Book>()
    private val sequence = AtomicLong(0)

    fun list(): List<Book> = books.values.sortedBy { it.id }

    fun get(id: Long): Book = books[id] ?: throw BaseExceptionUtils.notFound("图书不存在：$id")

    fun create(form: BookForm): Book {
        val id = sequence.incrementAndGet()
        val book = form.toBook(id, LocalDateTime.now())
        books[id] = book
        return book
    }

    fun update(id: Long, form: BookForm): Book {
        // 创建时间不随修改变化
        val createTime = get(id).createTime
        val book = form.toBook(id, createTime)
        books[id] = book
        return book
    }

    fun delete(id: Long) {
        // 先确认存在，不存在时返回 404
        get(id)
        books.remove(id)
    }

    private fun BookForm.toBook(id: Long, createTime: LocalDateTime): Book = Book(
        id = id,
        title = title,
        author = author,
        isbn = isbn,
        price = price,
        stock = stock,
        createTime = createTime,
    )
}
