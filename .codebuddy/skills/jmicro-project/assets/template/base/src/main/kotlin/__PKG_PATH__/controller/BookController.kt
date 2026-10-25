package __PKG__.controller

import com.jiangtj.micro.auth.annotations.HasPermission
import com.jiangtj.micro.auth.annotations.HasRole
import com.jiangtj.micro.common.schema.JsonSchema
import com.jiangtj.micro.common.schema.JsonSchemaGenerator
import __PKG__.book.Book
import __PKG__.book.BookForm
import __PKG__.book.BookService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 图书管理接口（示例）：
 * - 查询：`book:read` 权限
 * - 新增/修改：`book:write` 权限
 * - 删除：`admin` 角色
 */
@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService,
) {

    @HasPermission("book:read")
    @GetMapping
    fun list(): List<Book> = bookService.list()

    @HasPermission("book:read")
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Book = bookService.get(id)

    /**
     * 返回 [BookForm] 的 JSON Schema，前端可据此动态生成表单
     */
    @GetMapping("/form-schema")
    fun formSchema(): JsonSchema = JsonSchemaGenerator.generate<BookForm>()

    @HasPermission("book:write")
    @PostMapping
    fun create(@Valid @RequestBody form: BookForm): Book = bookService.create(form)

    @HasPermission("book:write")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody form: BookForm): Book =
        bookService.update(id, form)

    @HasRole("admin")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        bookService.delete(id)
    }
}
