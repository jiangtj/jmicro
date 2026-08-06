# J Micro Auth

![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-auth/api)

当你需要对 Spring Boot 应用进行权限认证与鉴权时，可能会选择 Spring Security、Apache Shiro 或 SaToken 等框架，按文档一步步搭建。但假如不引入框架、从零搭建鉴权模块，你会发现大部分情况下**一个 filter 就足够了**。那些框架真的必要吗？这是本模块的设计初衷：做更少的事——仅仅是对 filter 的扩展，再提供我们熟知的注解。这就是它做的事。

> 定位：轻量、filter / AOP 中心化（非 Spring Security-first）、支持 Servlet 与 WebFlux 双栈。

## 目录

- [如何使用](#如何使用)
  - [配置转换器](#配置转换器)
  - [授权控制](#授权控制)
  - [处理授权上下文](#处理授权上下文)
  - [匹配方式](#匹配方式)
  - [多账号体系](#多账号体系)
  - [自定义注解](#自定义注解)
- [OIDC 能力](#oidc-能力)
- [测试](#测试)
- [DEMO](#demo)

## 如何使用

### 引入依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-auth</artifactId>
</dependency>
```

> 建议配合 `micro-dependencies` BOM 使用，省略版本号（见[根 README](../README.md)）。

### 配置转换器

第一步，实现一个 `AuthContextConverter`，将请求转换为鉴权上下文。下面使用 JJWT 解析 bearer token 作为示例：

```java
@Component
public class JsonAuthContextConverter implements AuthContextConverter {
    @Nullable
    @Override
    public AuthContext convert(AuthRequest request) {
        List<String> headers = request.getHeaders(AuthRequestAttributes.TOKEN_HEADER_NAME);
        if (headers.size() != 1) {
            return null;
        }
        String token = headers.get(0);
        JwtParser parser = Jwts.parser()
            .verifyWith(key)
            .build();
        Claims body = parser.parseSignedClaims(token).getPayload();
        Subject subject = new Subject();
        subject.setId(body.sub);
        // 如果你在 session 中存储登录信息，只需从 session 取出并转换为 AuthContext 即可
        // 角色和权限从你的服务中获取（tip: 缓存可以带来更好的性能）
        return AuthContext.create(subject, Authorization.create(roles, permissions));
    }
}
```

### 授权控制

配置好转换器后，即可在任意位置进行权限控制：

- 授权类 `AuthService` / `AuthReactiveService`：可在过滤器或业务代码中使用。
- 注解 `@HasLogin` / `@HasRole` / `@HasPermission`：控制方法或类级别的权限。

```java
@Service
class ExampleService {
    @HasPermission("permission key")
    public void hasAnyPermission(){
        // authService.hasPermission("permission key") 与注解写法保持一致
        // do something
    }
}
```

### 处理授权上下文

常见场景：认证与授权过程分离（如 Casdoor 集成），认证后需补全授权信息，此时使用 `AuthContextHandler`：

```java
@Order(Number)
@Component
public class ProvideRoleHandler implements AuthContextHandler {
    @Override
    public void handle(AuthContext ctx) {
        // 获取你的角色
        ctx.setAuthorization(Authorization.create(roles));
    }
}
```

### 匹配方式

默认匹配规则：判断提供的 `key` 是否存在于授权上下文中。针对 `permission` 还提供 `ant-style` 权限匹配，添加如下配置即可：

```properties
jmicro.auth.permission-match=ant
```

你也可以覆盖默认授权类，实现任意鉴权方式（`AuthUtils` 可帮助简化权限判断）：

```java
import com.jiangtj.micro.auth.servlet.DefaultAuthService;

@Service
public class AuthService extends DefaultAuthService {
    @Override
    public void hasPermission(@NonNull Logic logic, @NonNull String... permissions) {
        /**
         * a:*   -> a:a
         * b:**  -> b:b:b
         * c:*:c -> c:c:c
         */
        AuthUtils.hasAntPermission(getContext(), logic, permissions);
    }
}
```

### 多账号体系

`AuthContextConverter` 的返回值可以为 `null`：返回 `null` 时会继续使用下一个转换器。因此很容易实现多账号体系——编写多个转换器各自处理授权上下文，不支持的返回 `null` 交由后续处理。建议通过 `Subject` 的 `issuer` 与 `type` 区分，例如 Casdoor 模块中 `issuer` 为授权服务器地址、`type` 为固定的 `casdoor`。

### 自定义注解

注解扩展性很高，可通过组合注解方式自定义：

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HasRole(value = {"admin", "user"}, logic = Logic.OR)
public @interface HasAdminOrUser {
}
// 那么 @HasAdminOrUser 等价于 @HasRole(value = {"admin", "user"}, logic = Logic.OR)
```

也可以通过提供的切面类 `AnnotationMethodBeforeAdvice` / `ReactiveAnnotationMethodBeforeAdvice` 与 `AnnotationPointcut`（不限于鉴权）实现，以下是 `@HasRole` 的实现过程：

```java
public class HasRoleAdvice extends AnnotationMethodBeforeAdvice<HasRole> {
    @Resource
    private AuthService authService;

    @Override
    public Class<HasRole> getAnnotationType() {
        return HasRole.class;
    }

    @Override
    public void before(List<HasRole> annotations, Method method, Object[] args, @Nullable Object target) {
        for (HasRole annotation : annotations) {
            authService.hasRole(annotation.logic(), annotation.value());
        }
    }
}

@Configuration
public class MyConfiguration {
    @Bean
    public HasRoleAdvice hasRoleAdvice() {
        return new HasRoleAdvice();
    }
    @Bean
    public Advisor hasRoleAdvisor(HasRoleAdvice advice) {
        return new DefaultPointcutAdvisor(new AnnotationPointcut<>(HasRole.class), advice);
    }
}
```

## OIDC 能力

`micro-auth` 内置 **OIDC 轻量认证**支持（包 `com.jiangtj.micro.auth.oidc`），提供：

- **API 鉴权**：基于 OIDC 的 Bearer Token 校验能力。
- **SPA OIDC 认证**：面向单页应用的 OIDC 认证流程支持。

可选的 **OIDC Server（Cas）** 能力位于 `micro-business` 模块（包 `com.jiangtj.micro.business.oidc.cas`，默认关闭），它复用本模块提供的 OIDC 基础能力，可作为你构建自有鉴权模块的参考实现。

## 测试

`micro-test` 提供测试支持，使用 `@WithMockUser`（或 `@WithMockSubject` / `@WithMockRole` / `@WithMockPermission`）即可模拟用户进行集成测试：

```java
@JMicroTest
@AutoConfigureWebTestClient
class Test {

    @Resource
    WebTestClient client;

    @Test
    @WithMockUser(subject = "user", roles = {"roleA", "roleB"})
    void getRole() {
        client.build().get().uri("/")
            .exchange()
            .expectStatus().isOk();
    }
}
```

响应式中，service 层需要权限时，可用 `AuthStepVerifier` 替换 `StepVerifier`，它会自动提供授权上下文；也可通过 `TestAuthContextHolder` 获取上下文自行注入：

```java
@JMicroTest
class Test {
    @Test
    @WithMockUser(subject = "user", roles = {"roleA", "roleB"})
    void getRole() {
        AuthStepVerifier.create(someService.call())
            .expectComplete()
            .verify();
    }
}
```

> 详细测试能力见 [micro-test/README.md](../micro-test/README.md)。

## DEMO

- [使用 WeChat OAuth2.0 实现的小程序认证服务，集成 Json Web Token](https://github.com/jiangtj-lab/jmicro-demo-wechat)
