package __PKG__.book

import __PKG__.common.toPage
import __PKG__.common.withDefaultSort
import com.jiangtj.micro.web.BaseExceptionUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * 图书增删改查（内存实现，便于项目零依赖启动）。
 *
 * 接口契约与 demo 保持一致：列表走 `GET /books/page`（分页 + 过滤 + 排序），
 * 入参为 [BookQuery]，返回 Spring Data `Page`。
 *
 * 接入数据库后（`--features db`）：引入 Flyway + jOOQ，用生成的 `BookDao` 替换本类实现
 * （见 `references/database.md` 的 demo 版本），对外方法签名不变。
 */
@Service
class BookService {

    private val books = ConcurrentHashMap<Long, Book>()
    private val sequence = AtomicLong(0)

    private val comparators: Map<String, Comparator<Book>> = mapOf(
        "id" to compareBy { it.id },
        "title" to compareBy { it.title },
        "author" to compareBy { it.author },
        "price" to compareBy { it.price },
        "stock" to compareBy { it.stock },
        "createTime" to compareBy { it.createTime },
    )

    fun list(): List<Book> = books.values.sortedBy { it.id }

    /**
     * 图书分页查询。
     *
     * 排序字段按属性名（驼峰）传入，如 `sortQ=createTime,desc`，未指定时按主键倒序（新书在前）。
     * 查询条件中 [BookQuery.title] 模糊匹配，[BookQuery.minPrice]/[BookQuery.maxPrice] 为价格区间，
     * 其余字段等值匹配。
     */
    fun page(pageable: Pageable, query: BookQuery = BookQuery()): Page<Book> {
        val filtered = books.values
            .asSequence()
            .filter { book -> query.title.matchesFuzzy(book.title) }
            .filter { book -> query.author.matchesExact(book.author) }
            .filter { book -> query.isbn.matchesExact(book.isbn) }
            .filter { book -> query.stock.matchesExact(book.stock) }
            .filter { book -> query.minPrice?.let { book.price >= it } ?: true }
            .filter { book -> query.maxPrice?.let { book.price <= it } ?: true }
            .toList()
        val sort = pageable.withDefaultSort(Sort.by(Sort.Direction.DESC, "id")).sort
        val sorted = filtered.sortedWith(sort.toBookComparator())
        return sorted.toPage(pageable)
    }

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

    /**
     * 按 [Sort] 组合排序；未识别的属性忽略，全部忽略时回退为主键倒序。
     */
    private fun Sort.toBookComparator(): Comparator<Book> {
        var result: Comparator<Book>? = null
        for (order in this) {
            val comparator = comparators[order.property] ?: continue
            val directed = if (order.isAscending) comparator else comparator.reversed()
            result = result?.thenComparing(directed) ?: directed
        }
        return result ?: comparators.getValue("id").reversed()
    }

    private fun String?.matchesFuzzy(value: String?): Boolean =
        this?.trim()?.takeIf { it.isNotEmpty() }?.let { value?.contains(it, ignoreCase = true) == true } ?: true

    private fun <T> T?.matchesExact(value: T?): Boolean = this == null || this == value

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
