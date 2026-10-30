package com.jiangtj.micro.demobackend.book

import com.jiangtj.micro.test.JMicroTest
import com.jiangtj.micro.test.WithMockUser
import com.jiangtj.micro.web.BaseException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

/**
 * 图书 Service 集成测试（真实 Spring 上下文 + H2，不经过 Controller）。
 *
 * 权限通过 [WithMockUser] 表达：图书读/写对应 `book:read`、`book:write`，删除对应 `admin` 角色。
 * 写库操作由 Spring Test 的 [Transactional] 在用例结束后自动回滚，无需手工清理数据。
 */
@JMicroTest
@Transactional
@WithMockUser(roles = ["admin"], permissions = ["book:read", "book:write"])
class BookServiceTest {

    @Autowired
    lateinit var bookService: BookService

    @Test
    fun `seeded books are listed`() {
        val books = bookService.list()
        assertThat(books).isNotEmpty
        assertThat(books.map { it.id }).doesNotContainNull()
    }

    @Test
    fun `create read update delete`() {
        val created = bookService.create(
            BookForm("测试驱动开发", "Kent Beck", "9787111639428", BigDecimal("69.00"), 2),
        )
        val id = requireNotNull(created.id) { "新增后未回填主键" }
        assertThat(bookService.get(id).title).isEqualTo("测试驱动开发")

        val updated = bookService.update(
            id,
            BookForm("测试驱动开发（第2版）", "Kent Beck", "9787111639428", BigDecimal("79.00"), 5),
        )
        assertThat(updated.title).isEqualTo("测试驱动开发（第2版）")
        assertThat(updated.stock).isEqualTo(5)

        val loaded = bookService.get(id)
        assertThat(loaded.price).isEqualByComparingTo(BigDecimal("79.00"))
        assertThat(bookService.list().map { it.id }).contains(id)

        bookService.delete(id)
        val ex = assertThrows<BaseException> { bookService.get(id) }
        assertThat(ex.statusCode.value()).isEqualTo(404)
        assertThat(ex.body.detail).isEqualTo("图书不存在：$id")
    }

    @Test
    fun `delete missing book throws not found`() {
        val ex = assertThrows<BaseException> { bookService.delete(-1) }
        assertThat(ex.statusCode.value()).isEqualTo(404)
        assertThat(ex.body.detail).isEqualTo("图书不存在：-1")
    }
}
