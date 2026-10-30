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
        client.get().uri("/books").exchange().expectStatus().isOk();
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

## 断言 ProblemDetail

```java
client.get().uri("/books")
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
        client.get().uri("/books").exchange()
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
