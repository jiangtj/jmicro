package com.jiangtj.micro.demobackend.book

import com.jiangtj.micro.demobackend.auth.LoginController
import com.jiangtj.micro.test.JMicroRestTestClientMvcConfiguration
import com.jiangtj.micro.test.ProblemDetailMvcConsumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import java.math.BigDecimal

/**
 * 未使用 `@JMicroMvcTest`：它会把上下文固定为测试注入的 mock 用户，
 * 这里需要验证真实的登录 + token 解析流程。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JMicroRestTestClientMvcConfiguration::class)
class BookControllerTest {

    @Autowired
    lateinit var client: RestTestClient

    private fun login(username: String, password: String): String {
        val body = client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(LoginController.LoginRequest(username, password))
            .exchange()
            .expectStatus().isOk()
            .returnResult(LoginController.LoginResult::class.java)
            .responseBody
        return checkNotNull(body) { "login response body is null" }.token
    }

    @Test
    fun adminCanManageBooks() {
        val bearer = "Bearer " + login("admin", "admin123")

        val created = client.post().uri("/books")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BookForm("测试驱动开发", "Kent Beck", "9787111639428", BigDecimal("69.00"), 2))
            .exchange()
            .expectStatus().isOk()
            .returnResult(object : ParameterizedTypeReference<Map<String, Any>>() {})
            .responseBody

        checkNotNull(created) { "created book is null" }
        assertThat(created["id"]).isNotNull()
        val id = (created["id"] as Number).toLong()

        // 查
        client.get().uri("/books/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()

        // 改
        client.put().uri("/books/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BookForm("测试驱动开发（第2版）", "Kent Beck", "9787111639428", BigDecimal("79.00"), 5))
            .exchange()
            .expectStatus().isOk()

        // 列表
        client.get().uri("/books")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()

        // 删
        client.delete().uri("/books/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()

        // 删除后再查 -> 404
        client.get().uri("/books/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    fun readerCanReadButNotDelete() {
        val bearer = "Bearer " + login("reader", "reader123")

        client.get().uri("/books")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()

        // 删除需要 admin 角色
        client.delete().uri("/books/{id}", 1)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isForbidden()

        // 当前登录用户信息
        client.get().uri("/me")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.username").isEqualTo("reader")
            .jsonPath("$.roles[0]").isEqualTo("reader")
    }

    @Test
    fun anonymousCannotRead() {
        client.get().uri("/books")
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.unLogin().expect())
    }

    @Test
    fun loginWithWrongPassword() {
        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(LoginController.LoginRequest("admin", "wrong"))
            .exchange()
            .expectStatus().isUnauthorized()
    }
}
