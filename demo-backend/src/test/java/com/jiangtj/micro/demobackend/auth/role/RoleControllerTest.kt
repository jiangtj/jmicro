package com.jiangtj.micro.demobackend.auth.role

import com.jiangtj.micro.demobackend.controller.LoginController
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

/**
 * 角色增删改查与权限查询接口测试
 *
 * 与 `BookControllerTest` 一致，走真实的登录 + token 解析流程，不使用 mock 用户。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JMicroRestTestClientMvcConfiguration::class)
class RoleControllerTest {

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

    private fun adminBearer(): String = "Bearer " + login("admin", "admin123")

    @Test
    fun adminCanManageRoles() {
        val bearer = adminBearer()

        // 增
        val created = client.post().uri("/roles")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .body(RoleForm("tester", "测试角色", listOf("book:read")))
            .exchange()
            .expectStatus().isOk()
            .returnResult(object : ParameterizedTypeReference<Map<String, Any>>() {})
            .responseBody
        checkNotNull(created) { "created role is null" }
        val id = (created["id"] as Number).toLong()
        assertThat(created["code"]).isEqualTo("tester")

        // 查（详情含权限编码）
        client.get().uri("/roles/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.code").isEqualTo("tester")
            .jsonPath("$.name").isEqualTo("测试角色")
            .jsonPath("$.permissionCodes[0]").isEqualTo("book:read")

        // 列表
        val roles = checkNotNull(
            client.get().uri("/roles")
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .exchange()
                .expectStatus().isOk()
                .returnResult(object : ParameterizedTypeReference<List<Map<String, Any>>>() {})
                .responseBody,
        ) { "role list is null" }
        assertThat(roles.map { it["code"] }).contains("admin", "reader", "tester")

        // 改（权限绑定整体覆盖）
        client.put().uri("/roles/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .body(RoleForm("tester", "测试角色（改）", listOf("book:read", "book:write")))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.name").isEqualTo("测试角色（改）")
            .jsonPath("$.permissionCodes.length()").isEqualTo(2)

        // 删
        client.delete().uri("/roles/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()

        // 删除后再查 -> 404
        client.get().uri("/roles/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    fun createRoleWithDuplicateCodeOrUnknownPermissionFails() {
        val bearer = adminBearer()

        client.post().uri("/roles")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .body(RoleForm("admin", "重复角色"))
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.badRequest("角色编码已存在：admin"))

        client.post().uri("/roles")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .body(RoleForm("tester-unknown", "未知权限", listOf("not:exist")))
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.badRequest("未知的权限编码：not:exist"))
    }

    @Test
    fun adminCanQueryPermissions() {
        val bearer = adminBearer()

        val permissions = checkNotNull(
            client.get().uri("/permissions")
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .exchange()
                .expectStatus().isOk()
                .returnResult(object : ParameterizedTypeReference<List<Map<String, Any>>>() {})
                .responseBody,
        ) { "permission list is null" }
        assertThat(permissions.map { it["code"] }).contains("book:read", "book:write")

        // 按关键字过滤（匹配编码与名称）
        client.get().uri("/permissions?keyword={kw}", "write")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].code").isEqualTo("book:write")

        // 按编码查询单个权限
        client.get().uri("/permissions/{code}", "book:read")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.code").isEqualTo("book:read")
            .jsonPath("$.name").isEqualTo("图书查看权限")

        client.get().uri("/permissions/{code}", "not:exist")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    fun readerCannotManageRolesOrQueryPermissions() {
        val bearer = "Bearer " + login("reader", "reader123")

        client.get().uri("/roles")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.unRole("admin").expect())

        client.get().uri("/permissions")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectAll(ProblemDetailMvcConsumer.unRole("admin").expect())
    }

    /**
     * JWT 只保存角色，权限按角色解析并缓存：角色权限变化后缓存失效，
     * 已签发的 token 无需重新登录即可拿到新权限。
     */
    @Test
    fun rolePermissionChangeTakesEffectWithoutNewToken() {
        val admin = adminBearer()
        val reader = "Bearer " + login("reader", "reader123")
        val readerId = findRoleId(admin, "reader")

        // 预热缓存
        client.get().uri("/me")
            .header(HttpHeaders.AUTHORIZATION, reader)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.permissions.length()").isEqualTo(1)

        try {
            client.put().uri("/roles/{id}", readerId)
                .header(HttpHeaders.AUTHORIZATION, admin)
                .contentType(MediaType.APPLICATION_JSON)
                .body(RoleForm("reader", "读者", listOf("book:read", "book:write")))
                .exchange()
                .expectStatus().isOk()

            // 同一个 token，权限立即生效
            client.get().uri("/me")
                .header(HttpHeaders.AUTHORIZATION, reader)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.permissions.length()").isEqualTo(2)
                .jsonPath("$.permissions[1]").isEqualTo("book:write")
        } finally {
            // 还原初始数据，避免影响其它用例
            client.put().uri("/roles/{id}", readerId)
                .header(HttpHeaders.AUTHORIZATION, admin)
                .contentType(MediaType.APPLICATION_JSON)
                .body(RoleForm("reader", "读者", listOf("book:read")))
                .exchange()
                .expectStatus().isOk()
        }
    }

    private fun findRoleId(bearer: String, code: String): Long {
        val roles = checkNotNull(
            client.get().uri("/roles")
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .exchange()
                .expectStatus().isOk()
                .returnResult(object : ParameterizedTypeReference<List<Map<String, Any>>>() {})
                .responseBody,
        ) { "role list is null" }
        return (roles.first { it["code"] == code }["id"] as Number).toLong()
    }
}
