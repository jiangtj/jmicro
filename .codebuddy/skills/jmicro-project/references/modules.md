# 模块、坐标与自动配置

## 模块职责

| 模块 | 职责 | 是否有自动配置 |
| --- | --- | --- |
| `micro-common` | `JsonUtils`、日期常量、`FormRuleGenerator`、`JsonSchemaGenerator`、`@MobilePhone`/`@MinLength`/`@MaxLength`、`MicroException` 体系 | 否 |
| `micro-web` | `BaseException`/`BaseExceptionUtils`、拦截器函数式封装、AOP 注解基础设施、`JsonUtils.init(mapper)` | 是（`JMicroCommonAutoConfiguration`） |
| `micro-auth` | `AuthContextConverter`/`AuthContextHandler`、`AuthContext`/`Subject`/`Authorization`、`AuthService`、`@HasLogin/@HasSubject/@HasRole/@HasPermission`、`BCryptUtils`、OIDC 客户端 | 是（3 个） |
| `micro-sql-jooq` | `PageUtils`、`Dao`/`KtDao`、`GenerateHelper`、`ExtendGenerator` | 否 |
| `micro-business` | 系统配置 `JMicroConfig`、Flyway 扩展、图片上传、可选 OIDC Server(Cas) | 是（12 个） |
| `micro-test` | `@JMicroTest`、`@JMicroMvcTest`、`@WithMockUser` 系列、`ProblemDetailMvcConsumer` | 否（测试配置类） |
| `micro-dependencies` | `java-platform`，约束所有 `micro-*` 版本 | — |

## Maven 坐标

`group = com.jiangtj.micro`，`version` 取 Maven Central 上的发布版本（本文档对应 `0.4.0`，最新版本见
`https://repo1.maven.org/maven2/com/jiangtj/micro/micro-dependencies/maven-metadata.xml`）：

```
com.jiangtj.micro:micro-dependencies:<version>   # type=pom, import
com.jiangtj.micro:micro-common
com.jiangtj.micro:micro-web
com.jiangtj.micro:micro-auth
com.jiangtj.micro:micro-sql-jooq
com.jiangtj.micro:micro-business
com.jiangtj.micro:micro-test        # testImplementation
```

传递关系（影响"少写依赖"的判断）：

- `micro-auth` → `api(micro-web)`、`api(micro-common)`、`api(spring-boot-starter)`、`api(jjwt-api)`、`api(caffeine)`、`api(spring-security-crypto)`
- `micro-business` → `api(micro-auth)`、`api(micro-web)`、`api(spring-web)`；Flyway 与各图片 provider SDK 均为 `compileOnly`，需使用方自加

## Gradle 接入

```kotlin
repositories {
    mavenCentral()
    // 仅当使用本地发布的未发布版本时添加（放在 mavenCentral() 之前）：
    // mavenLocal()
}

dependencies {
    implementation(platform("com.jiangtj.micro:micro-dependencies:<version>"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:<boot-version>"))
    implementation("com.jiangtj.micro:micro-auth")
}
```

Maven 接入用 `dependencyManagement` + `<type>pom</type><scope>import</scope>`。

> 官方示例工程用 `io.spring.dependency-management` 插件 `imports { mavenBom(spring-boot-dependencies) }`；独立项目用 `platform(...)` 等价且更简单。
> `micro-dependencies` 只约束 `micro-*` 版本，**不含** Spring Boot BOM，因此 Boot BOM 必须自己 import。

## 版本对齐

| 组件 | 版本 |
| --- | --- |
| jmicro | `0.4.0` |
| Spring Boot | `4.1.1`（Boot 4 / Servlet 栈） |
| Kotlin | `2.4.20` |
| Java | 21 |

Boot 大版本与 jmicro 的编译目标不匹配时，会出现自动配置类缺失、`NoSuchMethodError` 等问题，优先对齐上表。

## Spring Boot 4 starter 名称差异

| 传统名称 | Boot 4 名称 |
| --- | --- |
| `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| `spring-boot-starter-test`（MVC 部分） | `spring-boot-webmvc-test` |
| RestClient 测试 | `spring-boot-starter-restclient-test` |

`DataSourcesProperties` 等包路径也有变化：Boot 4 为 `org.springframework.boot.jdbc.autoconfigure.DataSourceProperties`。

## 自动配置清单

`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 共 16 条：

- **micro-web**：`JMicroCommonAutoConfiguration` — 注入 `JsonMapper` 并 `JsonUtils.init(mapper)`，注册 `ApplicationProperty`。应用不要再自定义 `ObjectMapper`。
- **micro-auth**：
  - `AuthAutoConfiguration` — `@EnableConfigurationProperties(AuthProperties)`，注册 `AuthContextFactory`（收集所有 `AuthContextConverter`、`AuthContextHandler`）
  - `ServletAutoConfiguration` — 仅 Servlet 栈；注册默认 `AuthService`、`AuthHolder` 及 `@HasLogin`/`@HasSubject`/`@HasRole`/`@HasPermission` 四个 Advisor
  - `OpenIDConnectAutoConfiguration` — OIDC 客户端（JWKS / well-known → `JwtConverter`）
- **micro-business**：
  - `MicroFlywayAutoConfiguration` — 校验失败自动 clean（需 classpath 有 Flyway）
  - `JMicroConfigAutoConfiguration` — `JMicroConfigService` + 默认 `InMemoryJMicroConfigSaver`
  - `OidcServerAutoConfiguration` / `OidcServerServletAutoConfiguration` — 默认关闭
  - `PicUploadAutoConfiguration` — `PicUploadConfigLoader` + `PicUploadService`
  - provider：`LocalPicUploadAutoConfiguration`、`AliOSSAutoConfiguration`、`ObsAutoConfiguration`、`MinIOAutoConfiguration`、`EasyImagesAutoConfiguration`、`RustFSAutoConfiguration`

## 属性前缀

| 前缀 | 关键属性 | 默认 |
| --- | --- | --- |
| `jmicro.auth` | `permission-match`、`path-separator` | `Simple`（可选 `Ant`/`ant`）、`:` |
| `jmicro.jwt.oidc[]` | `pattern`、`matcher-style`、`jwks-uri`、`openid-configuration`、`order` | `*`、`ALWAYS`、`/`、`0` |
| `jmicro.flyway` | `clean-on-validation-error` | `false` |
| `jmicro.config` | `enabled`、`file-path`、`kv.*` | `false`、`upload` |
| `jmicro.pic.upload` | `enable`（唯一启动配置） | `true` |
| `jmicro.oidc.server` | `enabled` 等 | `false` |

业务自定义属性请使用独立前缀（如 `jmicro.app.jwt.*`），不要占用框架前缀。
