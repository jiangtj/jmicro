# 易错点与排查顺序

## 鉴权相关

1. **忘记实现 `AuthContextConverter`**
   现象：所有接口都 401 / `isLogin()` 恒 false。
   `AuthContextFactory` 不提供默认转换器；没有转换器时上下文恒为未登录。这是新项目最常见的“跑不起来”原因。

2. **转换器返回值用错**
   - 返回 `null` → 跳过，交给下一个转换器（多账号体系用）。
   - 返回 `AuthContext.unLogin()` → 直接终止，后面的转换器不会再执行。
   若同一应用需要同时支持“本系统 JWT”和“OIDC token”，两者都应只在自己的 token 特征匹配时返回上下文，否则返回 `null`。

3. **权限通配符不生效**
   默认 `jmicro.auth.permission-match=Simple`，只做精确包含判断。要 `book:*`/`book:**` 必须设 `jmicro.auth.permission-match=ant`。
   `AuthProperties` 的枚举值是 `Simple`/`Ant`，配置文件写小写 `ant`/`simple` 也可被绑定。

4. **401/403 没有响应体**
   未注册 `BaseException` → `ProblemDetail` 的 `@ControllerAdvice`。模板 `common/ApiExceptionHandler.kt` 即可，注意继承 `ResponseEntityExceptionHandler` 以保留 Spring 默认异常处理。

5. **Service 内部自调用失效**
   注解切面基于 AOP，同类内部 `this.xxx()` 调用不会触发 `@HasRole`/`@HasPermission` 校验。

6. **Kotlin 类没有被代理**
   缺少 `kotlin("plugin.spring")` 插件时，Kotlin 类默认 `final`，AOP 与 CGLIB 代理会失败或注解不生效。

## 配置相关

7. **`jmicro.config.enabled` 忘记开启**（默认 `false`）
   影响：`JMicroConfigService` 不注册、图片上传 provider 不注册、`jmicro.config.kv.*` 完全无效。

8. **属性前缀写错**
   框架前缀固定：`jmicro.auth.*`、`jmicro.jwt.*`、`jmicro.flyway.*`、`jmicro.config.*`、`jmicro.pic.upload.*`、`jmicro.oidc.server.*`。
   业务属性另起前缀（如 `jmicro.app.*`），避免与框架冲突。

9. **`@ConfigurationProperties` 未注册**
   要么加 `@Component`，要么在 `@Configuration` 上 `@EnableConfigurationProperties(Xxx::class)`；只加 `spring-boot-configuration-processor` 仅生成元数据，不会注册 Bean。

10. **不要自定义 `ObjectMapper` 替代 `JsonUtils`**
    自动配置已用 Spring 的 `JsonMapper` 初始化 `JsonUtils`；再建独立 mapper 会导致静态工具与 HTTP 响应序列化行为不一致。

## 构建/依赖

11. **Spring Boot 4 starter 名称**：`spring-boot-starter-webmvc`（不是 `-web`）、`spring-boot-webmvc-test`。
12. **`Could not find com.jiangtj.micro:micro-*:<version>`**
    该版本尚未发布到 Maven Central，或拼错了版本号。先用 `maven-metadata.xml` 确认可用版本，换成已发布版本。
    仅在需要未发布版本时，才由使用方本地发布（`publishToMavenLocal`）并加 `mavenLocal()`；jmicro 官方仓库默认开启 PGP 签名，本机无 key 时发布需加
    `-PsignAllPublications=false -PmavenCentralPublishing=false`。
13. **版本写错**：`micro-dependencies`、`micro-*` 版本必须一致，且与所用 Boot 大版本匹配（0.4.0 → Spring Boot 4.1.1 / Kotlin 2.4.21 / Java 21）。
    本技能模板针对 0.4.0；用 `--latest` 解析到更旧版本时会出现 `Unresolved reference`（`BCryptUtils`、`common.schema` 等新 API 尚不存在）。

## 数据相关

14. **Flyway 未生效**：`micro-business` 里 Flyway 是 optional，需自己加 `spring-boot-starter-flyway`；迁移脚本放 `src/main/resources/db/migration/`，命名沿用 `V1.0.0.sql` 或 `V1__init.sql`。
15. **jOOQ 类不存在**：生成代码依赖真实数据库结构，必须先启动/迁移数据库再跑 `db-generate` 标签的测试。
    生成目录用 demo 的 `detectOutputDirectory()` 自动探测（优先已存在当前包路径的目录），不要把 `src/main/java` 硬编码到 Kotlin 项目里。
16. **`jmicro.flyway.clean-on-validation-error=true`** 会清库，仅限 dev/test。

## 其它

17. **包扫描**：主类放在基础包根，`auth/`、`controller/`、`book/` 都是其子包；不要放到兄弟包。
18. **图片上传报“provider 未注册”**：先查 `jmicro.config.enabled=true`，再查对应 provider 的配置项前缀。
19. **上传大小 413**：`jmicro.pic.upload.max-file-size` 与 `spring.servlet.multipart.max-file-size`（默认 1MB，请求体还要看 `max-request-size`）需同时满足。

## 权限模型相关（最新 demo 的做法）

20. **把权限写进 JWT claims**
    现象：权限调整后必须等 token 过期/重新登录才生效；权限点一多 token 明显变大。
    demo 已改为 token 只存角色、权限由 `RolePermissionResolver` 按角色解析（带缓存），`AuthContext` 用 `RoleBasedAuthorization` 承载。除非确实需要"签发即冻结权限"，否则不要把权限放进 token。

21. **改完角色权限不生效**
    `RolePermissionResolver` 按角色编码缓存（demo 是 Caffeine，5 分钟过期），`RoleService.update/delete` 必须调用 `invalidate`；改角色编码时新旧编码都要失效。
    测试里同样要清缓存：`@Transactional` 只回滚数据库，清不掉内存缓存。

22. **角色/权限表关联数据残留**
    `sys_user_role`、`sys_role_permission` 没建外键，删角色前要显式删除关联（demo 的 `SysRoleRepository.deleteRelations`），否则出现孤立记录。

23. **主代码引用 `*.jooq.*` 生成类导致编译失败**
    生成类来自真实库结构，必须先 `./gradlew test --tests "*DBGenerateTest*"` 生成后再改主代码（模板默认给内存实现，正是为了首次编译能过）。

## 分页/接口契约相关（最新 demo）

24. **分页参数对不上**
    demo 约定：请求 `page` 从 1 开始、排序参数名为 `sortQ`（pojo 驼峰属性名，如 `sortQ=createTime,desc`），
    响应为 `{content, page:{size, number, totalElements, totalPages}}`。对应配置：
    `spring.data.web.pageable.one-indexed-parameters=true`、`spring.data.web.sort.sort-parameter=sortQ`、
    `spring.data.web.pageable.serialization-mode=VIA_DTO`。少配任一项会表现为"页码错一位 / 传 `sort` 不排序 / 响应结构带 pageable"。
    另外需要 `org.springframework.boot:spring-boot-data-commons` 依赖，否则 `Pageable` 参数无法解析：
    `No primary or single unique constructor found for interface Pageable`（500）。
25. **无分页列表端点已被移除**
    demo 的 books / roles / users 列表统一为 `GET .../page`（`BookController` 不再有 `GET /books`）。
    沿用旧接口会 404/405；新写业务也照 `/page` + `@ModelAttribute Query` + 返回 `Page` 的约定走。
26. **表单 schema 断言/字段对不上**
    `JsonSchemaGenerator.generate<XxxForm>()` 的 `title` 是类名（如 `BookForm`），必填项在 `required`；
    可空字段（如 `UserForm.password`）不在 `required` 里，"新增必填、修改可空"的差异要由前端按场景处理。

## 排查顺序

1. `./gradlew compileKotlin` → 编译/依赖问题（11–13、23）
2. 启动日志 `APPLICATION FAILED TO START` → 属性绑定、Bean 缺失（7–9、17）
3. 请求 401 → 转换器（1–2）
4. 请求 403 → 权限匹配与角色数据（3、20–22）
5. 请求 5xx 无响应体 → ControllerAdvice（4）
6. 数据/迁移 → 14–16、22
7. 分页/接口契约 → 24–26
