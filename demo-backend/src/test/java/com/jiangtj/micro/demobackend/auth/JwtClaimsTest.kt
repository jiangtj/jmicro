package com.jiangtj.micro.demobackend.auth

import com.jiangtj.micro.demobackend.controller.LoginController
import com.jiangtj.micro.test.JMicroRestTestClientMvcConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import tools.jackson.databind.json.JsonMapper
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * JWT 体积优化后的契约：
 * - token 只携带角色，不携带权限（权限点多时 token 不会变大）
 * - 权限在需要时按角色解析（`GET /me`、`@HasPermission`），并带缓存
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JMicroRestTestClientMvcConfiguration::class)
class JwtClaimsTest {

    @Autowired
    lateinit var client: RestTestClient

    private val jsonMapper: JsonMapper = JsonMapper.builder().build()

    private fun login(username: String, password: String): LoginController.LoginResult {
        val body = client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(LoginController.LoginRequest(username, password))
            .exchange()
            .expectStatus().isOk()
            .returnResult(LoginController.LoginResult::class.java)
            .responseBody
        return checkNotNull(body) { "login response body is null" }
    }

    /**
     * 解码 JWT 的 payload（不校验签名，仅用于断言 claims 内容）
     */
    private fun payloadOf(token: String): Map<*, *> {
        val json = String(Base64.getUrlDecoder().decode(token.split(".")[1]), StandardCharsets.UTF_8)
        return jsonMapper.readValue(json, Map::class.java)
    }

    @Test
    fun tokenOnlyCarriesRoles() {
        val result = login("admin", "admin123")

        // 登录响应仍返回权限，前端无需再发请求
        assertThat(result.permissions).contains("book:read", "book:write")

        val payload = payloadOf(result.token)
        assertThat(payload.keys).doesNotContain("permissions")
        assertThat(payload["roles"]).isEqualTo(listOf("admin"))
    }

    @Test
    fun permissionsResolvedFromRolesOnDemand() {
        val bearer = "Bearer " + login("reader", "reader123").token

        // /me 返回按角色解析出的权限
        client.get().uri("/me")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.roles[0]").isEqualTo("reader")
            .jsonPath("$.permissions[0]").isEqualTo("book:read")

        // @HasPermission 校验同样依赖按角色解析出的权限
        client.get().uri("/books")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
    }
}
