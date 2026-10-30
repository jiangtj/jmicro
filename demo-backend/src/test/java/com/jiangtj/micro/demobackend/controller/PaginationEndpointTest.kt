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
 * 分页接口的 MVC 层测试：验证 `page`/`size`/`sort` 参数绑定与统一分页响应结构
 * （`content`/`page`/`size`/`totalElements`/`totalPages`/`first`/`last`）。
 */
@JMicroMvcTest
class PaginationEndpointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser(roles = ["admin"], permissions = ["book:read"])
    fun `books page binds pageable and returns page result`() {
        mockMvc.perform(
            get("/books/page")
                .param("page", "0")
                .param("size", "1")
                .param("sort", "id,asc"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(3))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(false))
    }

    @Test
    @WithMockUser(roles = ["admin"], permissions = ["book:read"])
    fun `books page filters by keyword`() {
        mockMvc.perform(get("/books/page").param("keyword", "虚拟机"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].title").value("深入理解 Java 虚拟机"))
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun `roles page returns roles with permissions`() {
        mockMvc.perform(get("/roles/page").param("size", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.content[0].permissionCodes").isArray)
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun `permissions page returns permissions`() {
        mockMvc.perform(get("/permissions/page").param("page", "1").param("size", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content[0].code").value("book:write"))
            .andExpect(jsonPath("$.last").value(true))
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun `system configs page slices config items`() {
        mockMvc.perform(get("/system/configs/page").param("size", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").isNumber)
    }
}
