# J Micro

![status](https://img.shields.io/badge/status-developing-yellow.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1d836355f32d423cb487081709b5890d)](https://app.codacy.com/gh/jiangtj/jmicro/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
![Maven Central Version](https://img.shields.io/maven-central/v/com.jiangtj.micro/parent)
![Java](https://img.shields.io/badge/java-17-007396?logo=openjdk)
![Kotlin](https://img.shields.io/badge/kotlin-2.2-7F52FF?logo=kotlin)
![Spring Boot](https://img.shields.io/badge/spring%20boot-4.x-6DB33F?logo=springboot)

J Micro 是一个基于 Spring Boot 的轻量级基础工具集，帮助开发者更轻松地构建应用（提供基于 Vue 的前后端分离 Demo）。它由 [JCPlatform](https://github.com/JiangTJ/jc-platform) 拆分而来，专注于基础应用能力；后续也将支撑另一个面向微服务的项目。

> 设计理念：做更少的事，只做必要的扩展（如基于过滤器与注解的鉴权），不强依赖特定框架，支持 Servlet 与 WebFlux 双栈。

## 目录

- [模块总览](#模块总览)
- [快速开始](#快速开始)
  - [本地安装](#本地安装)
  - [引入依赖管理](#引入依赖管理)
- [核心模块](#核心模块)
  - [micro-auth 认证与鉴权](#micro-auth-认证与鉴权)
  - [micro-web Web 工具](#micro-web-web-工具)
  - [micro-common 通用工具](#micro-common-通用工具)
  - [micro-pic-upload-starter 图片上传](#micro-pic-upload-starter-图片上传)
  - [micro-sql-jooq 数据库扩展](#micro-sql-jooq-数据库扩展)
  - [micro-business 业务聚合](#micro-business-业务聚合)
  - [micro-spring-boot-starter 应用默认配置](#micro-spring-boot-starter-应用默认配置)
  - [micro-test 测试支持](#micro-test-测试支持)
  - [micro-dependencies 依赖对齐（BOM）](#micro-dependencies-依赖对齐bom)
- [Demo 应用](#demo-应用)
  - [运行 Demo](#运行-demo)
  - [配置 Casdoor](#配置-casdoor)
- [构建与发布](#构建与发布)

## 模块总览

| 模块 | 类型 | 说明 |
| --- | --- | --- |
| `micro-common` | library | JSON、日期、表单校验规则、基础校验注解与常用工具类 |
| `micro-web` | library | 过滤器/拦截器扩展、`FluentWebFilter`、异常基础能力 |
| `micro-auth` | library | 轻量认证与鉴权（filter + 注解 + OIDC 基础能力） |
| `micro-spring-boot-starter` | starter | 应用默认配置（异常处理 + Web 过滤器自动装配） |
| `micro-sql-jooq` | library | JOOQ 业务封装（分页、代码生成扩展） |
| `micro-pic-upload-starter` | starter | 图片上传（本地 / OSS / OBS / MinIO / EasyImages） |
| `micro-business` | library | 业务聚合（Flyway 扩展、可选 OIDC Server） |
| `micro-test` | library | 集成测试支持（`@JMicroTest`、`@WithMockUser` 等） |
| `micro-dependencies` | bom (platform) | 内部模块版本对齐表，供使用方 `import` |
| `demo-backend` | demo | Servlet 后端示例（端口 17001） |
| `demo-reactive` | demo | WebFlux 后端示例（端口 17001） |

> 所有 `micro-*` 库模块均已发布至 Maven Central；`demo-*` 仅为示例应用，不在发布范围内。

## 快速开始

### 本地安装

开发阶段建议本地安装：克隆仓库后执行以下命令，将各模块发布到本地 Maven 仓库（需要 `maven-publish` 插件，已在所有 `micro-*` 模块中配置）：

```shell
./gradlew publishToMavenLocal
```

### 引入依赖管理

添加依赖管理（BOM），可在引入各模块时省略版本号（推荐）：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.jiangtj.micro</groupId>
            <artifactId>micro-dependencies</artifactId>
            <version>${last-version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

随后按需引入模块依赖，例如：

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-auth</artifactId>
</dependency>
```

## 核心模块

### micro-auth 认证与鉴权

[![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)](./micro-auth)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-auth/api)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-auth</artifactId>
</dependency>
```

通过 `AuthContextConverter` 将请求转换为鉴权上下文，即可使用过滤器或注解进行权限控制：

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
      // 如果你在 session 中存储登录信息，只需从中取出并转换为 AuthContext 即可
      // 角色和权限从你的服务中获取（tip: 缓存可以带来更好的性能）
      return AuthContext.create(subject, Authorization.create(roles, permissions));
   }
}
```

配置好转换器后，即可通过授权类（`AuthService` / `AuthReactiveService`）或注解（`@HasLogin` `@HasRole` `@HasPermission`）控制权限：

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

`micro-auth` 内置 OIDC 轻量认证支持（API 鉴权 + SPA OIDC 认证，包 `com.jiangtj.micro.auth.oidc`）；可选的 OIDC Server（Cas）能力位于 `micro-business` 模块（`com.jiangtj.micro.business.oidc.cas`，默认关闭），可参考其实现构建自己的鉴权模块。

> 详细用法（多账号体系、自定义注解、授权上下文处理、测试）见 [micro-auth/README.md](./micro-auth/README.md)。

### micro-web Web 工具

[![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)](./micro-web)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-web/api)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-web</artifactId>
</dependency>
```

在响应式开发中，`FluentWebFilter` 让过滤器配置变得轻松：

```java
@EnableFluentWebFilter
@Configuration
public class MyConfiguration {
    @Bean
    public FluentWebFilter fluentWebFilter(AuthReactiveService authReactiveService) {
        return FluentWebFilter.create()
            .exclude("/", "/login")
            .action((exchange, chain) ->
                authReactiveService.hasLogin().then(chain.filter(exchange)))
            .path("/roleA/**").action((exchange, chain) ->
                authReactiveService.hasRole("roleA").then(chain.filter(exchange)));
    }
}
```

> Tip: 这是对过滤器的扩展，不只可用于鉴权——任何希望拦截的场景都可以使用。传统 Servlet 中仍可使用 Spring 提供的、支持 ant-style 路径匹配的拦截器。
>
> 若使用 Spring Boot 3.5+，也可使用官方 `@FilterRegistration`：`@FilterRegistration(name = "my-filter", urlPatterns = "/test/*", order = 0)`

> 更多内容见 [micro-web/README.md](./micro-web/README.md)。

### micro-common 通用工具

[![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)](./micro-common)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-common/api)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-common</artifactId>
</dependency>
```

提供 JSON 处理、日期格式化、表单校验规则生成、基础校验注解与常用工具类，支持 Java 与 Kotlin。

#### FormRule 规则生成

通过 Java Bean Validation 生成 [Async Validator](https://github.com/yiminghe/async-validator) 表单校验规则：

```java
Map<String, List<FormRule>> generate = FormRuleGenerator.generate(Example.class);
```

> 完整功能与示例见 [micro-common/README.md](./micro-common/README.md)。

### micro-pic-upload-starter 图片上传

[![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)](./micro-pic-upload-starter)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-pic-upload-starter/api)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-pic-upload-starter</artifactId>
</dependency>
```

图片上传常用且繁琐，本模块旨在简化上传：配置好参数即可将图片转换为可访问 URL，支持以下服务商：

- [x] 本地上传
- [x] 阿里云 OSS
- [x] 华为云 OBS
- [x] MinIO (aka S3)
- [x] EasyImages 2.0

> 配置方式与代码示例见 [micro-pic-upload-starter/README.md](./micro-pic-upload-starter/README.md)。

### micro-sql-jooq 数据库扩展

[![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)](./micro-sql-jooq)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-sql-jooq/api)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-sql-jooq</artifactId>
</dependency>
```

为什么不用 MyBatis？JOOQ 可以像写 Java 一样写 SQL，并具备良好的类型校验。本模块提供通用 DAO、分页查询、Lombok POJO 生成等工具。例如分页查询：

```java
public Page<AdminUser> fetchPage() {
    return PageUtils.selectFrom(create, ADMIN_USER)
        .conditions(condition(new AdminUserRecord(user)))
        .pageable(pageable)
        .fetchPage(AdminUser.class);
}
```

同样提供响应式支持：

```java
@Bean
public DSLContext dslContext(ConnectionFactory connectionFactory) {
    return DSL.using(connectionFactory);
}

public Mono<Page<AdminUser>> fetchPage() {
    return PageUtils.selectFrom(create, ADMIN_USER)
        .conditions(condition(new AdminUserRecord(user)))
        .pageable(pageable)
        .subscribe(Flux::from, Mono::from)
        .map(PageReactiveUtils.toPage(AdminUser.class));
}
```

> 更多用法（代码生成、`ExtendGenerator`）见 [micro-sql-jooq/README.md](./micro-sql-jooq/README.md)。

### micro-business 业务聚合

[![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)](./micro-business)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-business/api)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-business</artifactId>
</dependency>
```

业务侧自动配置与扩展聚合模块，内置：

- **Flyway 扩展**（可选依赖）：校验失败时提供可选的自动清理能力，适合开发/测试环境快速重建。
- **OpenID Connect 服务器（Cas）能力**（可选，默认关闭）：提供 JWKS、Well-known 配置与授权/令牌端点，复用 `micro-auth` 的 OIDC 基础能力。

Flyway 为可选能力，需自行引入依赖：

```xml
<!-- Flyway 为可选能力，需自行引入 -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>
```

> 完整配置与扩展指南见 [micro-business/README.md](./micro-business/README.md)。

### micro-spring-boot-starter 应用默认配置

[![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)](./micro-spring-boot-starter)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-spring-boot-starter/api)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-spring-boot-starter</artifactId>
</dependency>
```

提供应用默认配置：启用 `micro-web` 的过滤器（引入 starter 后默认启用），并对异常做统一处理（遵循 RFC 9457 Problem Details）。

> 详见 [micro-spring-boot-starter/README.md](./micro-spring-boot-starter/README.md)。

### micro-test 测试支持

[![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)](./micro-test)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-test/api)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-test</artifactId>
    <scope>test</scope>
</dependency>
```

为模块提供集成测试支持：`@JMicroTest` 组合注解与基于注解的模拟用户（`@WithMockUser` / `@WithMockSubject` / `@WithMockRole` / `@WithMockPermission`），以及响应式中替换 `StepVerifier` 的 `AuthStepVerifier`。

> 详见 [micro-test/README.md](./micro-test/README.md)。

### micro-dependencies 依赖对齐（BOM）

`micro-dependencies` 是一个 Gradle `java-platform`（等价于 Maven BOM），集中约束所有 `micro-*` 模块的版本，供使用方通过依赖管理 `import` 实现版本对齐。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.jiangtj.micro</groupId>
            <artifactId>micro-dependencies</artifactId>
            <version>${last-version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

> 版本对齐规则见 [micro-dependencies/build.gradle.kts](./micro-dependencies/build.gradle.kts)。

## Demo 应用

项目提供两个功能一致的示例后端（前后端分离，前端为 Vue），均默认运行在端口 `17001`：

- [demo-backend](demo-backend)：基于 Servlet 的后端服务
- [demo-reactive](demo-reactive)：基于 WebFlux 的后端服务

两者提供一致功能，部署在同一端口。运行 Demo 时，请**二选一**启动。

### 运行 Demo

```shell
# Servlet 版
./gradlew :demo-backend:bootRun

# 或 WebFlux 版
./gradlew :demo-reactive:bootRun
```

### 配置 Casdoor

Demo 的认证与鉴权由 [Casdoor](https://casdoor.org/) 提供，需先准备一个 Casdoor 实例。

1. 运行以下命令创建体验版 Casdoor（线上部署请参考官方文档配置数据库）：

    ```shell
    docker run -p 28000:8000 -d casbin/casdoor-all-in-one
    ```

    > 开发环境 `demo-backend` 使用端口 `28000`（而非 `8000`），以避免端口冲突。

2. 在「身份认证 → 应用」中添加应用：
    - 应用名：`application_he3oml`
    - client-id：`a1f9883530433d009fb1`
    - client-secret：`a7b03bfdb051d1e6115dad3ec304995f975900eb`

配置完成后运行后端与前端的 Demo 项目即可体验。详见根目录 [docker-compose.yml](./docker-compose.yml)。

## 构建与发布

```shell
# 本地安装所有库模块到 Maven Local
./gradlew publishToMavenLocal

# 运行全部模块测试
./gradlew test

# 构建并发布到 Maven Central（需配置 gradle.properties 中的签名与仓库信息）
./gradlew publish
```

`demo-*` 应用不应用 `maven-publish` 插件，不会被发布。

---

许可证：[LGPL-2.1](./LICENSE)
