# 认证与鉴权（micro-auth）

## 核心模型

| 类型 | 包 | 说明 |
| --- | --- | --- |
| `AuthRequest` | `com.jiangtj.micro.auth.context` | 请求抽象：`getPath()`、`match(patterns)`、`getHeader(name)`（返回 `Optional`）、`getHeaders(name)`、`getQueryParam(s)`、`getSessionAttribute(name)` |
| `AuthContext` | 同上 | `subject()`、`authorization()`、`isLogin()`；静态方法 `create(subject, authorization)`、`unLogin()` |
| `Subject` | 同上 | `builder().id().name().displayName().type().issuer().build()` |
| `Authorization` | 同上 | `create(roles)`、`create(roles, permissions)`、`roles()`、`permissions()`；也可自己实现（demo 的 `RoleBasedAuthorization` 让 `permissions()` 按角色惰性解析） |
| `AuthContextConverter` | 同上 | 把请求转换为上下文，带 `@Order` 决定顺序 |
| `AuthContextHandler` | 同上 | 认证后补全授权信息（认证/授权分离场景） |
| `AuthService` | `com.jiangtj.micro.auth.core` | `subject`/`authorization` getter、`hasLogin()`、`hasRole(roles)`、`hasRole(Logic.AND/OR, roles)`、`hasPermission(...)` |
| `AuthRequestAttributes` | `com.jiangtj.micro.auth` | `TOKEN_HEADER_NAME`、`TOKEN_HEADER_PREFIX` |
| `BCryptUtils` | 同上 | `encode(raw)`、`matches(raw, encoded)`、`isEncoded(value)` |

## 转换器契约

```kotlin
@Component
class MyAuthContextConverter : AuthContextConverter, Ordered {
    override fun getOrder(): Int = 0
    override fun convert(request: AuthRequest): AuthContext { /* ... */ }
}
```

- 返回 `null`：跳过本转换器，交给下一个（多账号体系靠这个实现，用 `Subject.issuer`/`type` 区分）。
- 返回 `AuthContext.unLogin()`：**终止转换**，直接判定未登录。仅当该请求明确属于本账号体系（如带了本系统 token 但校验失败）时使用。
- 返回 `null` 时上游 `AuthContextFactory` 最终会得到未登录上下文，同样触发 401——区别只在是否会尝试后续转换器。

## 注解

| 注解 | 作用 |
| --- | --- |
| `@HasLogin` | 需登录 |
| `@HasSubject` | 校验 subject 的 `id`/`type`/`issuer` 等 |
| `@HasRole` | 校色，`logic = Logic.AND/OR` 控制多值关系 |
| `@HasPermission` | 权限，同样支持 `logic` |

类级与方法级都可用；注解可组合（自定义注解上叠加 `@HasRole` 即等价）。Service 层同样可标注（Advisor 基于注解切面，不限于 Controller）。

## 匹配模式

```properties
# 默认 Simple：判断提供的 key 是否存在于授权上下文
jmicro.auth.permission-match=ant
```

`ant` 模式下支持 `a:*`、`b:**`、`c:*:c`；`jmicro.auth.path-separator` 控制分隔符（默认 `:`）。

demo 的权限点与前端一一对应，用默认的 `simple` 精确匹配；只有在需要 `book:*` 这类通配时才改 `ant`。
权限 code 里带分隔符（如 `book:read`）不影响 simple 匹配，它只做字符串包含判断。

## 最小 JWT 登录实现（模板即此结构，与最新 demo 一致）

1. `JwtProperties`：`@ConfigurationProperties("jmicro.app.jwt")`，含 `issuer`、`secret`（HS256 至少 32 字节）、`ttl: Duration`。
2. `JwtService`：`Jwts.builder()...signWith(key, Jwts.SIG.HS256).compact()` 签发；`Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload` 解析，捕获 `JwtException`/`IllegalArgumentException` 返回 `null`。
3. `AuthContextConverter`：从 `AuthRequestAttributes.TOKEN_HEADER_NAME` 取头，去掉 `TOKEN_HEADER_PREFIX` 前缀后解析，成功则 `AuthContext.create(subject, RoleBasedAuthorization(roles, resolver::resolve))`。
4. `POST /login` 校验密码（`BCryptUtils.matches`）并签发 token；`GET /me` 用 `@HasLogin` + `AuthService.subject/authorization` 回显。

**token 里只放角色，不放权限**（demo 的做法）：

```kotlin
// 只写 roles claim
.claim(JwtService.CLAIM_ROLES, user.roles)

// 鉴权信息：权限在首次访问时才按角色解析，结果由 resolver 缓存
class RoleBasedAuthorization(
    private val roles: List<String>,
    private val resolvePermissions: (Collection<String>) -> List<String>,
) : Authorization {
    private val permissions: List<String> by lazy { resolvePermissions(roles) }
    override fun roles(): List<String> = roles
    override fun permissions(): List<String> = permissions
}
```

好处：权限点变多不会撑大 token；调整角色权限后，已签发的 token 无需重新登录即可生效（权限是每次请求解析的）。
把权限写进 claims 只适合"权限极少且变更不频繁"的场景——一旦要在运行期改权限，就必须让用户重新登录，容易踩坑。

## 角色 / 权限落库（demo 结构）

demo 的账号体系已从内存改为数据库（迁移脚本见 `references/database.md`）：

| 表 | 说明 |
| --- | --- |
| `sys_user` | 账号，`password` 存 bcrypt 哈希，`enabled` 控制禁用 |
| `sys_role` | 角色，`code` 唯一 |
| `sys_permission` | 权限字典，由可重复迁移脚本 `R__sys_permission.sql` 维护 |
| `sys_user_role` | 用户-角色关联（不建外键，应用层维护） |
| `sys_role_permission` | 角色-权限关联，直接存 `permission_code`（不建外键） |

配套类（demo 中位于 `auth/user`、`auth/role`、`auth/permission`、`dao`）：

- `UserStore`：查用户 → 校验 bcrypt → 查角色 → 按角色解析权限，失败抛 `BaseExceptionUtils.unauthorized("用户名或密码错误")`。
- `RolePermissionResolver`：按角色编码解析权限并缓存（Caffeine），提供 `resolve(roles)` 与 `invalidate(roleCode)`。
- `RoleService` / `PermissionService` + `RoleController` / `PermissionController`：角色增删改查（权限绑定整体覆盖）、权限字典查询。
- `SysRoleRepository` / `SysPermissionRepository`：用 `DSLContext` 写关联查询（按用户查角色、按角色批量查权限、覆盖写绑定、删角色时清理关联），避免 N+1。

```kotlin
// 落库版 RolePermissionResolver：按角色缓存，角色权限变更时精确失效
@Component
class RolePermissionResolver(private val permissionRepository: SysPermissionRepository) {

    private val cache: Cache<String, List<String>> = Caffeine.newBuilder()
        .maximumSize(1_000)
        .expireAfterWrite(Duration.ofMinutes(5))
        .build()

    fun resolve(roles: Collection<String>): List<String> = roles
        .filter { it.isNotBlank() }
        .distinct()
        .asSequence()
        .map { role -> cache.get(role) { permissionRepository.findCodesByRoleCodes(listOf(role)) } }
        .flatten()
        .distinct()
        .sorted()
        .toList()

    fun invalidate(roleCode: String) = cache.invalidate(roleCode)
}
```

要点：

- `RoleService.update/delete` 之后必须 `invalidate` 该角色（改编码时新旧编码都要清），否则权限变更在缓存过期（默认 5 分钟）前不生效。
- 角色权限绑定用"先删后插"整体覆盖；`sys_role_permission` 未建外键，删除角色时要一并清理 `sys_user_role` / `sys_role_permission`。
- 新增权限点改 `R__sys_permission.sql` 即可，Flyway 检测到校验和变化会重新执行。
- 落库实现依赖 jOOQ 生成的 DAO/表类：**先生成代码再替换主代码实现**，否则 `compileKotlin` 会因缺少生成类失败（见 `references/database.md`）。生成前保持模板的内存实现即可。

## 自定义鉴权方式

覆盖 `AuthService` 即可，`AuthUtils.hasAntPermission(ctx, logic, permissions)` 可复用 ant 匹配：

```kotlin
@Service
class AuthService : DefaultAuthService() {
    override fun hasPermission(logic: Logic, vararg permissions: String) {
        AuthUtils.hasAntPermission(context, logic, *permissions)
    }
}
```

## 错误响应

鉴权失败抛 `BaseException`（`BaseExceptionUtils.unauthorized/forbidden`），需配合 ControllerAdvice 才能变成 RFC 9457 响应体：

```kotlin
@ControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(RuntimeException::class)
    fun handle(e: RuntimeException): ProblemDetail = e.toProblemDetail()
}
```

## OIDC

- 客户端能力在 `com.jiangtj.micro.auth.oidc`，配置 `jmicro.jwt.oidc[]`（`jwks-uri` 或 `openid-configuration`），内置 `JwtConverter` 完成 token → 上下文转换。
- 自建授权服务器用 `micro-business` 的 `jmicro.oidc.server.*`（默认关闭），一般项目无需启用。
