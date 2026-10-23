package com.jiangtj.micro.demobackend.book;

import com.jiangtj.micro.auth.annotations.HasPermission;
import com.jiangtj.micro.auth.annotations.HasRole;
import com.jiangtj.micro.demobackend.jooq.tables.pojos.Book;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 图书管理接口
 * <ul>
 *     <li>查询：需要 book:read 权限</li>
 *     <li>新增/修改：需要 book:write 权限</li>
 *     <li>删除：需要 admin 角色</li>
 * </ul>
 */
@RestController
@RequestMapping("/books")
public class BookController {

    @Resource
    private BookService bookService;

    @HasPermission("book:read")
    @GetMapping
    public List<Book> list() {
        return bookService.list();
    }

    @HasPermission("book:read")
    @GetMapping("/{id}")
    public Book get(@PathVariable Long id) {
        return bookService.get(id);
    }

    @HasPermission("book:write")
    @PostMapping
    public Book create(@Valid @RequestBody BookForm form) {
        return bookService.create(form);
    }

    @HasPermission("book:write")
    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @Valid @RequestBody BookForm form) {
        return bookService.update(id, form);
    }

    @HasRole("admin")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}
