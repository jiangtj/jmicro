
# J Micro

![author](https://img.shields.io/badge/author-mrtt-blue.svg)
[![email](https://img.shields.io/badge/email-jiang.taojie@foxmail.com-blue.svg)](mailto:jiang.taojie@foxmail.com)
![status](https://img.shields.io/badge/status-developing-yellow.svg)
![Maven Central Version](https://img.shields.io/maven-central/v/com.jiangtj.micro/parent)

J Micro 是一个基于 Spring Boot 的轻量框架，方便开发者更轻松的开发应用(提供基于 vue 的前后端分离的 Demo), 由 [JCPlatform](https://github.com/JiangTJ/jc-platform) 拆分出来专于基础应用的工具集，之后也将重构另一个项目，基于该项目专注微服务应用。

## 使用

在开发阶段，建议使用本地安装。即 `clone` 后，`mvn install -P lib` 安装到本地，当然也可直接引用 Maven Central 上已发布的lib

该项目包含下面这些模块

### [认证与鉴权模块](micro-auth)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-auth</artifactId>
    <version>${last-version}</version>
</dependency>
```

下面是一个例子，使用 jjwt 解析 bearer token

```java
public class JsonAuthContextConverter implements AuthContextConverter {

    @Override
    public AuthContext convert(HttpRequest request) {
        List<String> headers = request.getHeaders().get(AuthRequestAttributes.TOKEN_HEADER_NAME);
        if (headers == null || headers.size() != 1) {
            return AuthContext.unLogin();
        }

        String token = headers.get(0);
        JwtParser parser = Jwts.parser()
            .verifyWith(key)
            .build();
        Claims body = parser.parseSignedClaims(token).getPayload();
        Subject subject = new Subject();
        subject.setId(body.sub);
        return AuthContext.create(subject);
    }

}
```

只需要配置好转换器，你就可以在项目使用鉴权功能，可以使用过滤器 由Spring管理的授权类(`AuthService`/`AuthReactiveService`)或者通过注解方式(`@HasLogin` `@HasRole` `@HasPermission`)管理你的应用

```java
@Service
class ExampleService {
    @HasPermission("permission key")
    public void hasAnyPermission(){
        // authService.hasPermission("permission key") 如果使用授权类的写法，是不是和注解很相似
        // do something
    }
}
```

`micro-auth-casdoor` 是这个鉴权模块的扩展，用于支持casdoor的token认证，你可以参考他创建自己的鉴权模块

### [Web 模块](micro-web)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-web</artifactId>
    <version>${last-version}</version>
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

### [JOOQ 扩展](micro-sql-jooq)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-sql-jooq</artifactId>
    <version>${last-version}</version>
</dependency>
```

为什么不使用 Mybatis? 在中国 Mybatis 无疑是使用人数最多的，但多并不代表的好，我不喜欢在 xml 中写 sql, 因为它不能很好的对类型进行校验, 而 Jooq 可以像写 java 一样写 sql, 这对我来说很有吸引力, 这个扩展是提供一些方便的工具，比如分页查询

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
        .biSubscribe((listS, countS) -> Mono.zip(
            Flux.from(listS).map(l -> l.into(AdminUser.class)).collectList(),
            Mono.from(countS).map(Record1::value1)))
        .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
}
```

### [Starter 模块](micro-spring-boot-starter)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-spring-boot-starter</artifactId>
    <version>${last-version}</version>
</dependency>
```

这个模块提供了一些默认配置，比如 web 模块中的 filter，如果你使用 starter，那么默认是启用的，另外对于异常，做了统一的处理 RFC 9457

## 一起开发

如果你希望参与到这个项目的开发，依照下面的步骤搭建好开发环境

### 创建 casdoor

由 casdoor 提供认证和鉴权功能，下面命令行创建一个体验版的casdoor，你在线上部署时，请参考官方文档配置数据库。

```shell
docker run -p 28000:8000 -d casbin/casdoor-all-in-one
```

*开发环境demo-backend设置的端口是28000，不采用8000是考虑到，8000可能会有端口冲突*

#### 创建应用

访问 http://localhost:28000/ 添加应用，然后获取 clientId 和 clientSecret (todo 待完善)

