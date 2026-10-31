package com.jiangtj.micro.demobackend.controller

import com.jiangtj.micro.test.JMicroMvcTest
import com.jiangtj.micro.test.WithMockUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * 分页接口的 MVC 层测试：验证 `page`/`size`/`sortQ` 参数绑定与 Spring Data `Page` 的响应结构
 * （`spring.data.web.pageable.serialization-mode=VIA_DTO`：`content` + `page.size`/`page.number`/
 * `page.totalElements`/`page.totalPages`）。
 *
 * 请求参数约定来自 `application.properties`：
 * - `spring.data.web.pageable.one-indexed-parameters=true`：`page` 从 1 开始，响应 `page.number` 仍从 0 开始
 * - `spring.data.web.sort.sort-parameter=sortQ`：排序参数名为 `sortQ`（默认的 `sort` 不再生效）
 */
@JMicroMvcTest
class PaginationEndpointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser(roles = ["admin"], permissions = ["book:read"])
    fun `books page binds one-indexed page and sortQ`() {
        mockMvc.perform(
            get("/books/page")
                .param("page", "1")
                .param("size", "1")
                .param("sortQ", "id,asc"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page.number").value(0))
            .andExpect(jsonPath("$.page.size").value(1))
            .andExpect(jsonPath("$.content.length()").value(1))
            // sortQ 生效：按 id 升序时首条为 id=1（默认排序为 id 倒序，会是 id=3）
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.page.totalElements").value(3))
            .andExpect(jsonPath("$.page.totalPages").value(3))
    }

    @Test
    @WithMockUser(roles = ["admin"], permissions = ["book:read"])
    fun `books page filters by title`() {
        mockMvc.perform(get("/books/page").param("title", "虚拟机"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].title").value("深入理解 Java 虚拟机"))
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun `roles page returns roles with permissions`() {
        mockMvc.perform(get("/roles/page").param("size", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.page.totalElements").value(2))
            .andExpect(jsonPath("$.page.totalPages").value(2))
            .andExpect(jsonPath("$.content[0].permissionCodes").isArray)
    }

    @Test
    @WithMockUser(roles = ["admin"], permissions = ["book:read"])
    fun `legacy sort parameter is ignored`() {
        mockMvc.perform(
            get("/books/page")
                .param("size", "1")
                .param("sort", "id,asc"),
        )
            .andExpect(status().isOk)
            // `sort` 不再是排序参数名，落入默认排序（id 倒序），首条为 id=3
            .andExpect(jsonPath("$.content[0].id").value(3))
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun `permissions page returns permissions`() {
        mockMvc.perform(get("/permissions/page").param("page", "2").param("size", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page.number").value(1))
            .andExpect(jsonPath("$.page.totalElements").value(2))
            .andExpect(jsonPath("$.page.totalPages").value(2))
            .andExpect(jsonPath("$.content[0].code").value("book:write"))
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun `system configs page slices config items`() {
        mockMvc.perform(get("/system/configs/page").param("size", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.page.totalElements").isNumber)
    }
}
