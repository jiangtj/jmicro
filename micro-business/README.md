# micro-business

jmicro 的业务能力聚合模块，用于承载通用的业务侧自动配置与扩展能力。当前内置了对 Flyway 的轻量级扩展与可选的 OpenID Connect 服务器（Cas）能力。

## 模块特性

- 继承 Spring Boot 默认自动配置体系，通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册。
- 内置 **Flyway 扩展**（可选依赖，见下文），在校验失败时提供可选的自动清理能力，适合开发、测试环境的快速重建。
- 内置 **OpenID Connect 服务器（Cas）能力**（可选，默认关闭，见下文），提供 JWKS、Well-known 配置与授权/令牌端点，复用 `micro-auth` 的 OIDC 基础能力（`com.jiangtj.micro.auth.oidc`）。

## 使用方法

### 添加依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-business</artifactId>
</dependency>
```

### Flyway 扩展（可选）

Flyway 相关依赖在 `micro-business` 中是**可选（optional）**的，模块本身不会强制传递 Flyway。
若要启用 Flyway 扩展，需在使用方自行引入 Flyway 依赖：

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-business</artifactId>
</dependency>
<!-- 可选能力：需自行引入 Flyway -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>
```

未引入 Flyway 时，`MicroFlywayAutoConfiguration` 因 `@ConditionalOnClass(Flyway.class)` 不会生效，模块可正常用于无 Flyway 的场景。

#### 配置属性

在 `application.properties` 或 `application.yml` 中添加配置：

```properties
# 是否在校验失败后自动清理并重新迁移
micro.flyway.clean-on-validation-error=true
```

```yaml
micro:
  flyway:
    clean-on-validation-error: true
```

`clean-on-validation-error` 默认为 `false`。建议仅在开发或测试环境启用该能力，避免误删生产数据。

#### 工作机制

当 Flyway 在执行 `migrate` 时发生 `FlywayValidateException`：

- `clean-on-validation-error=false`：直接抛出异常，行为与 Spring Boot 默认一致
- `clean-on-validation-error=true`：自动执行 `clean` 后重新 `migrate`

如果你需要完全关闭 Flyway，可继续使用 Spring Boot 的配置：

```properties
spring.flyway.enabled=false
```

### OpenID Connect 服务器（可选，默认关闭）

OIDC 服务器能力位于包 `com.jiangtj.micro.business.oidc.cas`，**默认不开启**，需显式配置 `jmicro.oidc.server.enabled=true` 才生效。它依赖 `micro-auth` 提供的 OIDC 基础能力（`com.jiangtj.micro.auth.oidc`）与 JJWT 运行时，并通过 `OidcKeyService` 实现 jjwt 的 `Locator<Key>` 接口与 `micro-auth` 解耦。

该能力同时提供 **Servlet**（`OidcServerServletAutoConfiguration`，基于 `RouterFunction`）与 **Reactive** 自动配置入口；仅当 `jmicro.oidc.server.enabled=true` 且对应 Web 应用类型成立时注册。

#### 添加依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-business</artifactId>
</dependency>
<!-- OIDC 服务器依赖的 OIDC 基础能力（由 micro-business 传递） -->
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-auth</artifactId>
</dependency>
```

#### 配置属性

```properties
# 开启 OpenID Connect 服务器（默认 false）
jmicro.oidc.server.enabled=true
# 基础 URL（用于 Well-known 中的 issuer 等）
jmicro.oidc.server.base-url=http://localhost:17001
# KID 前缀（可选，默认不添加）
jmicro.oidc.server.kid-prefix=
# Well-known 配置端点（默认 /oidc/.well-known/openid-configuration）
jmicro.oidc.server.well-known=/oidc/.well-known/openid-configuration
# 是否展示 Well-known 配置端点（默认 true）
jmicro.oidc.server.show-well-known=true
# JWKS URI 端点（默认 /oidc/jwks）
jmicro.oidc.server.jwks-uri=/oidc/jwks
# 授权端点（默认 /oidc/auth）
jmicro.oidc.server.authorization-endpoint=/oidc/auth
# 令牌端点（默认 /oidc/token）
jmicro.oidc.server.token-endpoint=/oidc/token

# 客户端配置列表
jmicro.oidc.server.clients[0].client-id=client1
jmicro.oidc.server.clients[0].client-secret=secret1
jmicro.oidc.server.clients[0].callback-uri[0]=http://localhost:17001/callback
```

对应配置类：`OidcServerProperties`（`jmicro.oidc.server.*`）与 `OidcServerClientProperties`（客户端条目）。

#### 工作机制

- `OidcServerAutoConfiguration`：在 `enabled=true` 时注册 `OidcKeyService`（启动时生成/刷新密钥），并实现 jjwt `Locator<Key>`，供 `micro-auth` 的 `OidcLocator` 通过类型查找复用。
- `OidcServerServletAutoConfiguration`（Servlet）：注册 `OidcEndpointService` 与其 `RouterFunction`，对外暴露 Well-known、JWKS、授权、令牌端点；并提供 `OidcRedirectAuth` 钩子（默认 `TODO`，需使用者自定义 `userInfo()` 实现以返回当前用户信息）。
- 未开启或依赖缺失时，相关自动配置因条件注解不生效，模块可正常用于无 OIDC 服务器的场景。

#### 扩展指南

- `OidcRedirectAuth` 默认实现为 `TODO`，实际接入时需提供自定义 Bean 实现 `userInfo()`，返回授权成功后注入令牌的用户声明。
- 密钥与 KID 由 `OidcKeyService` 在启动时刷新，可通过 `kid-prefix` 区分多实例。

## 扩展指南

新增业务能力时，请在 `com.jiangtj.micro.business` 下建立子包，遵循项目约定：

- 包级 `package-info.java` 标注 `@NullMarked`
- 通过 `@AutoConfiguration` 暴露自动配置，并在 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中注册
- 可选能力使用 `compileOnly` + `runtimeOnly` 声明依赖，避免强制传递给使用者
