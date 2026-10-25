package com.jiangtj.micro.demobackend.config

import com.jiangtj.micro.demobackend.controller.LoginController
import com.jiangtj.micro.demobackend.jooq.tables.daos.SysConfigDao
import com.jiangtj.micro.test.JMicroRestTestClientMvcConfiguration
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
 * 系统配置接口测试：覆盖读取默认值、写入持久化、列表、生成配置行、恢复默认与鉴权。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JMicroRestTestClientMvcConfiguration::class)
class JMicroConfigControllerTest {

    @Autowired
    lateinit var client: RestTestClient

    @Autowired
    lateinit var sysConfigDao: SysConfigDao

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
    fun `admin can persist and reset system config`() {
        val bearer = "Bearer " + login("admin", "admin123")

        // 未写入时返回默认值，且不暴露内部格式化函数
        client.get().uri("/system/configs/demo.site.name")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.value").isEqualTo("JMicro Demo")
            .jsonPath("$.isModified").isEqualTo(false)
            .jsonPath("$.group.name").isEqualTo("演示站点")
            .jsonPath("$.formatter").doesNotExist()
            .jsonPath("$.valueFormatter").doesNotExist()
            .jsonPath("$.jsonType").doesNotExist()

        // 写入覆盖值 -> 持久化到 sys_config
        client.put().uri("/system/configs/demo.site.name")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("value" to "我的站点"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.value").isEqualTo("我的站点")
            .jsonPath("$.isModified").isEqualTo(true)

        assertThat(sysConfigDao.fetchOneByConfigKey("demo.site.name")?.configValue).isEqualTo("我的站点")

        // 列表可见最新值
        val configs = client.get().uri("/system/configs")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .returnResult(object : ParameterizedTypeReference<List<Map<String, Any?>>>() {})
            .responseBody
        val item = checkNotNull(configs) { "config list is null" }
            .first { it["key"] == "demo.site.name" }
        assertThat(item["value"]).isEqualTo("我的站点")
        assertThat(item["isModified"]).isEqualTo(true)

        // 生成可粘贴到 application.properties 的配置行
        val properties = client.get().uri("/system/configs/demo.site.name/properties")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .returnResult(String::class.java)
            .responseBody
        assertThat(properties).isEqualTo("jmicro.config.kv.demo.site.name=我的站点")

        // 恢复默认值 -> 删除持久化记录
        client.delete().uri("/system/configs/demo.site.name")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.value").isEqualTo("JMicro Demo")
            .jsonPath("$.isModified").isEqualTo(false)

        assertThat(sysConfigDao.fetchOneByConfigKey("demo.site.name")).isNull()
    }

    @Test
    fun `invalid value is rejected`() {
        val bearer = "Bearer " + login("admin", "admin123")

        client.put().uri("/system/configs/demo.site.maintenance")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("value" to "not-a-boolean"))
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody()
            .jsonPath("$.title").isEqualTo("配置错误")
    }

    @Test
    fun `public configs are readable without login`() {
        val configs = client.get().uri("/system/configs/public")
            .exchange()
            .expectStatus().isOk()
            .returnResult(object : ParameterizedTypeReference<List<Map<String, Any?>>>() {})
            .responseBody
        val keys = checkNotNull(configs) { "public config list is null" }.map { it["key"] }
        assertThat(keys).containsExactlyInAnyOrder(
            "demo.site.name",
            "demo.site.description",
            "demo.site.theme",
            "demo.site.social-links",
        )
    }

    @Test
    fun `reader cannot manage system config`() {
        val bearer = "Bearer " + login("reader", "reader123")

        client.get().uri("/system/configs")
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange()
            .expectStatus().isForbidden()
    }
}
