package com.jiangtj.micro.demobackend.controller

import com.jiangtj.micro.auth.annotations.HasPermission
import com.jiangtj.micro.auth.annotations.HasRole
import com.jiangtj.micro.common.schema.JsonSchema
import com.jiangtj.micro.common.schema.JsonSchemaGenerator
import com.jiangtj.micro.demobackend.book.BookForm
import com.jiangtj.micro.demobackend.book.BookQuery
import com.jiangtj.micro.demobackend.book.BookService
import com.jiangtj.micro.demobackend.jooq.tables.pojos.Book
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 图书管理接口
 * - 查询：需要 book:read 权限
 * - 新增/修改：需要 book:write 权限
 * - 删除：需要 admin 角色
 */
@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService,
) {

    /**
     * 图书分页查询：过滤参数按 [BookQuery] 声明（`title` 模糊匹配，`author`/`isbn`/`stock` 精确匹配，
     * `minPrice`/`maxPrice` 价格区间）；
     * 分页参数 `page`（从 1 开始）、`size`、`sortQ`（驼峰属性名，如 `sortQ=createTime,desc`）
     *
     * 直接返回 Spring Data 的 `Page`，由 `spring.data.web.pageable.serialization-mode=VIA_DTO`
     * 序列化为 `{content, page:{size, number, totalElements, totalPages}}`。
     */
    @HasPermission("book:read")
    @GetMapping("/page")
    fun page(
        pageable: Pageable,
        @ModelAttribute query: BookQuery,
    ): Page<Book> = bookService.page(pageable, query)

    @HasPermission("book:read")
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Book = bookService.get(id)

    /**
     * 返回 [BookForm] 的 JSON Schema，供前端动态生成表单
     */
    @GetMapping("/form-schema")
    fun formSchema(): JsonSchema = JsonSchemaGenerator.generate<BookForm>()

    @HasPermission("book:write")
    @PostMapping
    fun create(@Valid @RequestBody form: BookForm): Book = bookService.create(form)

    @HasPermission("book:write")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody form: BookForm): Book = bookService.update(id, form)

    @HasRole("admin")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        bookService.delete(id)
    }
}
