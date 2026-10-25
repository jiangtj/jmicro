package __PKG__

import __PKG__.book.BookForm
import __PKG__.controller.AuthController
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
 * 验证真实登录 + token 解析链路。
 *
 * 这里不用 `@JMicroMvcTest`：它会把上下文固定为 mock 用户，测不到 `/login` 与 `AuthContextConverter`。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JMicroRestTestClientMvcConfiguration::class)
class AuthFlowTest {

    @Autowired
    lateinit var client: RestTestClient

    private fun login(username: String, password: String): String {
        val body = client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(AuthController.LoginRequest(username, password))
            .exchange()
            .expectStatus().isOk()
            .returnResult(AuthController.LoginResult::class.java)
            .responseBody
        return checkNotNull(body) { "login response body is null" }.token
    }

    @Test
    fun anonymousCannotReadBooks() {
        client.get().uri("/books")
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.unLogin().expect())
    }

    @Test
    fun loginWithWrongPassword() {
        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(AuthController.LoginRequest("admin", "wrong"))
            .exchange()
            .expectStatus().isUnauthorized()
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

        client.get().uri("/books/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()

        client.get().uri("/books")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()

        client.delete().uri("/books/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()

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

        client.delete().uri("/books/{id}", 1)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isForbidden()

        client.get().uri("/me")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.username").isEqualTo("reader")
            .jsonPath("$.roles[0]").isEqualTo("reader")
    }

    @Test
    fun bookFormSchema() {
        client.get().uri("/books/form-schema")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.type").isEqualTo("object")
            .jsonPath("$.properties.title.maxLength").isEqualTo(128)
            .jsonPath("$.required").isArray()
    }
}
