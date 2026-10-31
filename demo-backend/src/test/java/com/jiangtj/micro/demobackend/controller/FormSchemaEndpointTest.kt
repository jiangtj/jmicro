package com.jiangtj.micro.demobackend.controller

import com.jiangtj.micro.test.JMicroMvcTest
import com.jiangtj.micro.test.WithMockUser
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * 表单 JSON Schema 接口的 MVC 层测试：验证新增/修改表单对应的 `GET .../form-schema`
 * 返回 `JsonSchemaGenerator` 生成的结构（`$schema`、`title`、`properties`、`required`）。
 *
 * 对外可见性：`/books/form-schema` 为公开接口，`/users/form-schema`、`/roles/form-schema`
 * 需要 `admin` 角色（controller 上的 `@HasRole`）。
 */
@JMicroMvcTest
class FormSchemaEndpointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `book form schema is public`() {
        mockMvc.perform(get("/books/form-schema"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("BookForm"))
            .andExpect(jsonPath("$.properties.title.type").value("string"))
            .andExpect(jsonPath("$.properties.title.maxLength").value(128))
            .andExpect(jsonPath("$.properties.price.minimum").value(0))
            .andExpect(jsonPath("$.required").isArray)
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun `user form schema exposes validations`() {
        mockMvc.perform(get("/users/form-schema"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("UserForm"))
            .andExpect(jsonPath("$.properties.username.pattern").exists())
            .andExpect(jsonPath("$.properties.password.minLength").value(6))
            .andExpect(jsonPath("$.properties.roleIds.type").value("array"))
            .andExpect(jsonPath("$.properties.roleIds.items.type").value("integer"))
            // password 可空，新增时必填的差异由前端按场景处理
            .andExpect(jsonPath("$.required", not(hasItem("password"))))
    }

    @Test
    @WithMockUser(roles = ["admin"])
    fun `role form schema exposes validations`() {
        mockMvc.perform(get("/roles/form-schema"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("RoleForm"))
            .andExpect(jsonPath("$.properties.code.maxLength").value(64))
            .andExpect(jsonPath("$.properties.permissionCodes.items.type").value("string"))
            .andExpect(jsonPath("$.required.length()").value(3))
    }

    @Test
    fun `form schema requires admin role`() {
        mockMvc.perform(get("/users/form-schema"))
            .andExpect(status().isUnauthorized)
    }
}
