# J Micro Business

![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-business/api)

jmicro 的业务能力聚合模块，用于承载通用的业务侧自动配置与扩展能力。当前内置了系统配置（SystemConfig）、对 Flyway 的轻量级扩展，以及可选的 OpenID Connect 服务器（Cas）能力。

## 目录

- [模块特性](#模块特性)
- [使用方法](#使用方法)
  - [添加依赖](#添加依赖)
  - [系统配置 SystemConfig（可选）](#系统配置-systemconfig可选)
  - [Flyway 扩展（可选）](#flyway-扩展可选)
  - [OpenID Connect 服务器（可选，默认关闭）](#openid-connect-服务器可选默认关闭)
- [扩展指南](#扩展指南)

## 模块特性

- 继承 Spring Boot 默认自动配置体系，通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册。
- 内置 **系统配置（SystemConfig）能力**（可选，默认关闭，见下文），提供以键值对方式管理、持久化与动态刷新系统运行配置的能力，支持默认值加载、类型化表单（TEXT/SWITCH/SELECT 等）、配置变更事件与基于 Caffeine 的缓存。
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

> 建议配合 `micro-dependencies` BOM 使用，省略版本号（见[根 README](../README.md)）。

### 系统配置 SystemConfig（可选）

系统配置能力位于包 `com.jiangtj.micro.business.config`，**默认不开启**，需显式配置 `system.config.enabled=true` 才生效。它通过 `SystemConfigService` 集中管理配置项，支持从 `SystemConfigLoader` 加载默认值、通过 `SystemConfigSaver` 持久化覆盖值，并在变更时发布 `SystemConfigUpdateEvent` / `SystemConfigRefreshEvent` 事件。

默认实现提供了 `InMemorySystemConfigSaver`（基于 `ConcurrentHashMap` 的内存存储），并通过 `@ConditionalOnMissingBean` 注册，使用者可自定义 `SystemConfigSaver` Bean 接入数据库等持久化方案。

#### 配置属性

```properties
# 开启系统配置能力（默认 false）
system.config.enabled=true
# 配置文件路径（默认 upload）
system.config.file-path=upload
# 覆盖默认配置值的键值对
system.config.kv.site-name=JMicro
system.config.kv.max-upload-size=10MB
```

```yaml
system:
  config:
    enabled: true
    file-path: upload
    kv:
      site-name: JMicro
      max-upload-size: 10MB
```

#### 工作机制

- 启动时 `SystemConfigService` 收集所有 `SystemConfigLoader` 提供的 `SystemItemInfo` 作为默认配置，再应用 `system.config.kv` 中的覆盖值。
- 取值：`getValue(key)` / `isTrue(key)` 优先读取 `SystemConfigSaver` 中的覆盖值，未命中则回退到默认值；结果经 Caffeine 缓存加速，可通过 `refreshConfig()` 主动失效缓存。
- 写值：`updateConfig(key, value)` 会做值格式校验（`valueFormatter`），保存到 `SystemConfigSaver` 并发布 `SystemConfigUpdateEvent`；`deleteConfig(key)` 删除覆盖值并回退默认值，同时发布事件。
- `getAllConfig()` 返回排序后的配置视图（按分组 `group.order` 与 `order`），并对 `secret=true` 的项做脱敏（`******`），对带 `formatter` 的项生成 `formatedValue`。
- **bcrypt 支持（通过 formatter 实现）**：不再使用 `bcrypt` 配置项。密码类 `secret` 项应直接配置 `valueFormatter` 与 `formatter`，例如使用 `BCryptUtils.encodeFormatter`（写入明文时若非 bcrypt 哈希则哈希后持久化，已是哈希则透传，避免二次哈希）与 `BCryptUtils.maskFormatter`（展示时统一脱敏为 `******`）。读取校验明文可用 `BCryptUtils.matches(raw, stored)`。
- `getConfigByTag(tag)` 可按标签筛选配置项。

#### 扩展指南

- 实现 `SystemConfigLoader` 并注册为 Bean，可在启动时贡献一组默认配置项（含名称、分组、类型、表单渲染信息等）。
- 实现 `SystemConfigSaver` 并注册为 Bean（覆盖默认的内存实现），可将覆盖值持久化到数据库、配置中心等。
- 监听 `SystemConfigUpdateEvent` / `SystemConfigRefreshEvent` 可感知配置变化并做相应处理。

### Flyway 扩展（可选）

Flyway 相关依赖在 `micro-business` 中是**可选（optional）**的，模块本身不会强制传递 Flyway。若要启用 Flyway 扩展，需在使用方自行引入 Flyway 依赖：

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

```properties
# 是否在校验失败后自动清理并重新迁移
micro.flyway.clean-on-validation-error=true
```

```yaml
micro:
  flyway:
    clean-on-validation-error: true
```

`clean-on-validation-error` 默认为 `false`。**建议仅在开发或测试环境启用该能力，避免误删生产数据。**

#### 工作机制

当 Flyway 在执行 `migrate` 时发生 `FlywayValidateException`：

- `clean-on-validation-error=false`：直接抛出异常，行为与 Spring Boot 默认一致。
- `clean-on-validation-error=true`：自动执行 `clean` 后重新 `migrate`。

如果你需要完全关闭 Flyway，可继续使用 Spring Boot 的配置：

```properties
spring.flyway.enabled=false
```

### OpenID Connect 服务器（可选，默认关闭）

OIDC 服务器能力位于包 `com.jiangtj.micro.business.cas`，**默认不开启**，需显式配置 `jmicro.oidc.server.enabled=true` 才生效。它依赖 `micro-auth` 提供的 OIDC 基础能力（`com.jiangtj.micro.auth.oidc`）与 JJWT 运行时，并通过 `OidcKeyService` 实现 jjwt 的 `Locator<Key>` 接口与 `micro-auth` 解耦。

该能力当前提供 **Servlet** 自动配置入口（`OidcServerServletAutoConfiguration`，基于 Spring MVC 的 `RouterFunction`），并通过 `@ConditionalOnWebApplication(type = SERVLET)` 限定；仅当 `jmicro.oidc.server.enabled=true` 且为 Servlet Web 应用类型时注册。Reactive 自动配置入口尚未提供。

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
- 密钥使用 **ES384（EC P-384）** 算法生成，JWKS 与 ID Token 均基于该密钥；KID 在启动时生成（可由 `kid-prefix` 添加前缀区分实例）。
- 授权端点仅支持 **授权码模式（authorization_code）**，并支持 **PKCE（S256）** 或 **client_secret** 两种客户端校验方式；令牌端点校验授权码、`code_verifier` 或 `client_secret`，校验通过后签发带 KID 的 ES384 ID Token（同时作为 access_token），有效期 24 小时，授权码使用 Caffeine 缓存 15 分钟过期。
- 未开启或依赖缺失时，相关自动配置因条件注解不生效，模块可正常用于无 OIDC 服务器的场景。

#### 扩展指南

- `OidcRedirectAuth` 默认实现为 `TODO`，实际接入时需提供自定义 Bean 实现 `userInfo()`，返回授权成功后注入令牌的用户声明。
- 密钥与 KID 由 `OidcKeyService` 在启动时刷新，可通过 `kid-prefix` 区分多实例。

## 扩展指南

新增业务能力时，请在 `com.jiangtj.micro.business` 下建立子包，遵循项目约定：

- 包级 `package-info.java` 标注 `@NullMarked`
- 通过 `@AutoConfiguration` 暴露自动配置，并在 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中注册
- 可选能力使用 `compileOnly` + `runtimeOnly` 声明依赖，避免强制传递给使用者
- 当前已注册能力的子包：
  - `flyway`：Flyway 自动清理扩展（`MicroFlywayAutoConfiguration`）
  - `config`：系统配置能力（`SystemConfigAutoConfiguration`）
  - `cas`：OpenID Connect 服务器能力（`OidcServerAutoConfiguration`、`OidcServerServletAutoConfiguration`）
