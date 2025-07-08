# J Micro

![status](https://img.shields.io/badge/status-developing-yellow.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1d836355f32d423cb487081709b5890d)](https://app.codacy.com/gh/jiangtj/jmicro/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
![Maven Central Version](https://img.shields.io/maven-central/v/com.jiangtj.micro/parent)

J Micro 是一个基于 Spring Boot 的轻量框架，方便开发者更轻松的开发应用(提供基于 vue 的前后端分离的 Demo), 由 [JCPlatform](https://github.com/JiangTJ/jc-platform) 拆分出来专于基础应用的工具集，之后也将重构另一个项目，基于该项目专注微服务应用。

## 使用

在开发阶段，建议使用本地安装。即 `clone` 后，`mvn install -P lib` 安装到本地，当然也可直接引用 Maven Central 上已发布的 lib

```xml
<!-- 添加依赖管理，可以在添加模块的时候省略版本号 -->
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

### [认证与鉴权模块](micro-auth)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-auth</artifactId>
</dependency>
```

下面是一个例子，使用 jjwt 解析 bearer token

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
      // 如果你在 session 中存储登录信息，那么你只需从 session 中取出并转换为 AuthContext 即可
      // 获取角色和权限从你的服务中(tip: 缓存可以带来更好的性能)
      return AuthContext.create(subject, Authorization.create(roles, permissions));
   }
}
```

只需要配置好转换器，你就可以在项目使用鉴权功能，可以使用过滤器 由 Spring 管理的授权类(`AuthService`/`AuthReactiveService`)或者通过注解方式(`@HasLogin` `@HasRole` `@HasPermission`)管理你的应用

```java
@Service
class ExampleService {
    @HasPermission("permission key")
    public void hasAnyPermission(){
        // authService.hasPermission("permission key") 与注解的写法保持一致
        // do something
    }
}
```

`micro-auth-casdoor` 是这个鉴权模块的扩展，用于支持 casdoor 的 token 认证，你可以参考他创建自己的鉴权模块

### [Web 模块](micro-web)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-web</artifactId>
</dependency>
```

在响应式编程中，提供 `FluentWebFilter`，方便用户在过滤器中使用权限的场景，你可以向下面一样，十分轻松的实现过滤器的配置

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

> Tip: 这是对过滤器的扩展，所以不仅仅能用于鉴权，任何你希望过滤的场景，都可以使用这个过滤器，另外在传统的 servlet 中，可以使用拦截器，它由 Spring 提供的且支持 ant-style 的路径匹配
>
> 另外如果你使用 Spring Boot 3.5+, 你可以使用官方的 `@FilterRegistration` 例如
`@FilterRegistration(name = "my-filter", urlPatterns = "/test/*", order = 0)`

### [通用模块](micro-common)

#### FormRule 规则生成

通过 Java Bean Validation 生成对应的<a href="https://github.com/yiminghe/async-validator">Async Validator
表单校验规则</a>

```java
Map<String, List<FormRule>> generate = FormRuleGenerator.generate(Example.class);
```

### [图片上传模块](micro-pic-upload-starter)

```xml

<dependency>
   <groupId>com.jiangtj.micro</groupId>
   <artifactId>micro-pic-upload-starter</artifactId>
</dependency>
```

图片上传在项目中属于比较常用的功能，这个模块旨在简化上传，配置好参数，即可将你的图片转换为url，支持以下服务商上传图片

- [x] 本地上传
- [x] 阿里云 OSS
- [x] 华为云 OBS
- [x] MinIO (aka S3)
- [x] EasyImages 2.0

### [JOOQ 扩展](micro-sql-jooq)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-sql-jooq</artifactId>
</dependency>
```

为什么不使用 Mybatis? 在中国 Mybatis 无疑是使用人数最多的，但多并不代表的好，我不喜欢在 xml 中写 sql, 因为它不能很好的对类型进行校验,
而 Jooq 可以像写 java 一样写 sql, 这对我来说很有吸引力, 这个扩展是提供一些方便的工具，比如 通用的Dao 分页查询
支持Lombok的POJO生成 等等，例如分页查询可以如下写

```java
public Page<AdminUser> fetchPage() {
    return PageUtils.selectFrom(create, ADMIN_USER)
        .conditions(condition(new AdminUserRecord(user)))
        .pageable(pageable)
        .fetchPage(AdminUser.class);
}
```

我们还提供了对响应式开发的支持

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

### [Flyway 扩展](micro-flyway-starter)

修改 `spring.flyway.clean-on-validation-error` 为 `micro.flyway.clean-on-validation-error`, 在 `Spring Boot 3.5+` 中，支持在校验失败时清理数据库

### [Starter 模块](micro-spring-boot-starter)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-spring-boot-starter</artifactId>
</dependency>
```

这个模块提供了一些默认配置，比如 web 模块中的 filter，如果你使用 starter，那么默认是启用的，另外对于异常，做了统一的处理 RFC 9457

## Demo 应用

我们提供了一个 demo 应用，你可以参考它来快速开发你的应用，它总共分三个模块

- [demo-backend](demo-backend): Servlet 后端服务
- [demo-reactive](demo-reactive): Webflux 后端服务
- [demo-front](demo-front): Vue 前端服务

demo-backend 与 demo-reactive 是提供一样的功能，部署在同一端口上，所以，在你运行 demo 时，你需选择其中一个运行

### 创建 casdoor

casdoor 是需要的，demo 由 casdoor 提供认证和鉴权功能

1. 需要运行下面命令行创建一个体验版的 casdoor，你在线上部署时，请参考官方文档配置数据库
    ```shell
    docker run -p 28000:8000 -d casbin/casdoor-all-in-one
    ```
    > 开发环境 demo-backend 设置的端口是 28000，不采用 8000 是考虑到，8000 可能会有端口冲突

2. 登录 casdoor，并在身份认证->证书中，添加项目中的证书
3. 身份认证->应用中，添加应用
    - 填写应用名application_he3oml
    - 填写client-id a1f9883530433d009fb1
    - 填写client-secret a7b03bfdb051d1e6115dad3ec304995f975900eb
    - 证书选择新添加的证书

接下来运行前端和后端项目，即可体验
