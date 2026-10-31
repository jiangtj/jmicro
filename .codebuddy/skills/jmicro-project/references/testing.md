# 测试（micro-test）

## 依赖

```kotlin
testImplementation("com.jiangtj.micro:micro-test")
testImplementation("org.springframework.boot:spring-boot-webmvc-test")
testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
testImplementation("org.junit.jupiter:junit-jupiter")
```

## 注解

| 注解 | 等价于 |
| --- | --- |
| `@JMicroTest` | `@SpringBootTest` + `@ExtendWith(JMicroExtension)` + `@Import(JMicroConfiguration)` + `@ActiveProfiles("testcase")` |
| `@JMicroMvcTest` | `@JMicroTest` + `@AutoConfigureMockMvc` + `@Import(JMicroRestTestClientMvcConfiguration)`（注册 `RestTestClient`） |

`@JMicroTest` 会激活 `testcase` profile：项目若有按 profile 生效的必需配置，需提供 `application-testcase.properties`，否则用无 profile 依赖的配置。

## 模拟用户

```java
@JMicroMvcTest
class DemoTest {
    @Resource RestTestClient client;

    @Test
    @WithMockUser(subject = "1", roles = {"admin"}, permissions = {"book:read"})
    void shouldPass() {
        client.get().uri("/books/page").exchange().expectStatus().isOk();
    }
}
```

| 注解 | 作用 |
| --- | --- |
| `@WithMockUser` | subject + roles + permissions 全量 mock |
| `@WithMockSubject` / `@WithMockRole` / `@WithMockPermission` | 单项 mock |

上下文通过 `TestAuthContextHolder` 在线程内生效；Service 层测试可自行取值注入。

## Service 层集成测试（demo 风格）

demo 的测试以 Service 为入口（不经过 Controller），用 `@JMicroTest` + `@Transactional` + `@WithMockUser`：

```kotlin
@JMicroTest
@Transactional
@WithMockUser(roles = ["admin"])
class RoleServiceTest {

    @Autowired lateinit var roleService: RoleService
    @Autowired lateinit var rolePermissionResolver: RolePermissionResolver

    /**
     * 事务回滚只回滚数据库，内存缓存（Caffeine）必须显式清理，否则影响其它用例。
     */
    @AfterEach
    fun clearPermissionCache() {
        listOf("admin", "reader", "tester").forEach(rolePermissionResolver::invalidate)
    }

    @Test
    fun `create update delete`() {
        val created = roleService.create(RoleForm("tester", "测试角色", listOf("book:read")))
        assertThat(roleService.get(created.id).permissionCodes).containsExactly("book:read")
        roleService.delete(created.id)
        assertThat(roleService.list().map { it.code }).doesNotContain("tester")
    }
}
```

带鉴权注解的 Service/Controller 在 `@WithMockUser` 下才不会被 401/403 拦住；
权限变更的缓存失效、关联数据清理这类副作用，用这类测试覆盖最省事。

## 权限变更即时生效

JWT 只带角色，权限按角色解析并缓存，所以"改角色权限 → 已签发 token 立即拿到新权限"是可直接断言的：

```kotlin
@Test
fun `permission change takes effect without new token`() {
    val reader = roleService.list().first { it.code == "reader" }
    roleService.update(reader.id, RoleForm("reader", reader.name, listOf("book:read", "book:write")))
    assertThat(rolePermissionResolver.resolve(listOf("reader"))).containsExactly("book:read", "book:write")
}
```

## MVC 层：分页与表单 schema（demo 的 `PaginationEndpointTest` / `FormSchemaEndpointTest`）

分页参数绑定与响应结构只能在 MVC 层验证（`@JMicroMvcTest` + `MockMvc`），
表单 schema 则验证 `JsonSchemaGenerator` 的输出：

```kotlin
@JMicroMvcTest
class PaginationEndpointTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser(roles = ["admin"], permissions = ["book:read"])
    fun `page binds one-indexed page and sortQ`() {
        mockMvc.perform(get("/books/page").param("page", "1").param("size", "1").param("sortQ", "id,asc"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page.number").value(0))       // 请求 page=1 → 响应 number=0
            .andExpect(jsonPath("$.page.size").value(1))
            .andExpect(jsonPath("$.content.length()").value(1))
    }
}
```

```kotlin
@JMicroMvcTest
class FormSchemaEndpointTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `book form schema is public`() {
        mockMvc.perform(get("/books/form-schema"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("BookForm"))
            .andExpect(jsonPath("$.properties.title.maxLength").value(128))
            .andExpect(jsonPath("$.required").isArray)
    }
}
```

要点：

- 分页接口断言 `$.page.number`/`$.page.size`/`$.page.totalElements` 与 `$.content`（`VIA_DTO` 结构）；
  `sortQ` 生效可用"首条 id"间接断言，`sort` 参数已不再生效。
- `form-schema` 的断言用 `title`（类名）、`properties.*`、`required`；`password` 这类"新增必填、修改可空"的字段不会出现在 `required` 中。
- Service 层的用户/角色/配置测试用 `@JMicroTest` + `@Transactional` + `@WithMockUser(roles = ["admin"])`；
  写库操作自动回滚，但 Caffeine 缓存（`RolePermissionResolver`、`JMicroConfigService`）要在 `@AfterEach` 里显式清理。

## 断言 ProblemDetail

```java
client.get().uri("/books/page")
    .exchange()
    .expectAll(ProblemDetailMvcConsumer.unLogin().expect());
// 或
ProblemDetailMvcConsumer.expectProblemDetail(401, "需要登录");
```

## 测真实登录流程

`@JMicroMvcTest` 会把上下文固定为 mock 用户，无法验证 token 解析。需要真登录时直接组合：

```kotlin
@SpringBootTest
@AutoConfigureMockMvc
@Import(JMicroRestTestClientMvcConfiguration::class)
class AuthFlowTest {
    @Autowired lateinit var client: RestTestClient

    private fun login(username: String, password: String): String =
        client.post().uri("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(LoginRequest(username, password))
            .exchange()
            .expectStatus().isOk()
            .returnResult(LoginResult::class.java)
            .responseBody!!.token

    @Test
    fun anonymousGets401() {
        client.get().uri("/books/page").exchange()
            .expectAll(ProblemDetailMvcConsumer.unLogin().expect())
    }

    @Test
    fun readerCannotDelete() {
        val bearer = "Bearer " + login("reader", "reader123")
        client.delete().uri("/books/{id}", 1)
            .header(HttpHeaders.AUTHORIZATION, bearer)
            .exchange().expectStatus().isForbidden()
    }
}
```

- Boot 4 的导入：`org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`、`org.springframework.test.web.servlet.client.RestTestClient`。
- 用 `ParameterizedTypeReference` 读取 `Map`/集合类型的响应体。

## 覆盖清单建议

每个新增业务模块至少覆盖：未登录 401、权限不足 403、正常流程、校验失败 400、不存在 404（ProblemDetail 结构）。
