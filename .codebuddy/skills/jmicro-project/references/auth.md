# 认证与鉴权（micro-auth）

## 核心模型

| 类型 | 包 | 说明 |
| --- | --- | --- |
| `AuthRequest` | `com.jiangtj.micro.auth.context` | 请求抽象：`getPath()`、`match(patterns)`、`getHeader(name)`（返回 `Optional`）、`getHeaders(name)`、`getQueryParam(s)`、`getSessionAttribute(name)` |
| `AuthContext` | 同上 | `subject()`、`authorization()`、`isLogin()`；静态方法 `create(subject, authorization)`、`unLogin()` |
| `Subject` | 同上 | `builder().id().name().displayName().type().issuer().build()` |
| `Authorization` | 同上 | `create(roles)`、`create(roles, permissions)`、`roles()`、`permissions()` |
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

## 最小 JWT 登录实现（模板即此结构）

1. `JwtProperties`：`@ConfigurationProperties("jmicro.app.jwt")`，含 `issuer`、`secret`（HS256 至少 32 字节）、`ttl: Duration`。
2. `JwtService`：`Jwts.builder()...signWith(key, Jwts.SIG.HS256).compact()` 签发；`Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload` 解析，捕获 `JwtException`/`IllegalArgumentException` 返回 `null`。
3. `AuthContextConverter`：从 `AuthRequestAttributes.TOKEN_HEADER_NAME` 取头，去掉 `TOKEN_HEADER_PREFIX` 前缀后解析，成功则 `AuthContext.create(subject, Authorization.create(roles, permissions))`。
4. `POST /login` 校验密码（`BCryptUtils.matches`）并签发 token；`GET /me` 用 `@HasLogin` + `AuthService.subject/authorization` 回显。

角色与权限建议直接放进 JWT claims（无状态），但权限变更需重新签发 token；若需即时生效，用 `AuthContextHandler` 在每次请求时查库补全授权（配合缓存）。

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
