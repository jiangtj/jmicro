
# J Micro

![author](https://img.shields.io/badge/author-mrtt-blue.svg)
[![email](https://img.shields.io/badge/email-jiang.taojie@foxmail.com-blue.svg)](mailto:jiang.taojie@foxmail.com)
![status](https://img.shields.io/badge/status-developing-yellow.svg)
![Maven Central Version](https://img.shields.io/maven-central/v/com.jiangtj.micro/parent)

J Micro 是一个基于 Spring Boot 的轻量工具集，封装了常用功能，方便开发者快速开发(提供基于 vue 的前后端分离的 Demo), 由 [JCPlatform](https://github.com/JiangTJ/jc-platform) 拆分出来专于基础应用的工具集，之后也将重构另一个项目，基于该项目专注微服务应用。

### 使用

在开发阶段，建议使用本地安装。即 `clone` 后，`mvn install -P lib` 安装到本地，当然也可直接引用 Maven Central 上已发布的lib

该项目包含下面这些模块

#### [认证与鉴权模块](/micro-auth)

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-auth</artifactId>
    <version>${last-version}</version>
</dependency>
```

下面是一个例子，使用 jwt 解析 bearer token

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
        Subject subject = this.subject();
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

默认授权的匹配规则是判断提供的 `key` 是否在授权上下文中存在，当然针对 `permission` 也提供了一个 `ant-style` 权限匹配规则，你可以覆写默认的授权类实现

```java
import com.jiangtj.micro.auth.servlet.SimpleAuthService;

@Service
public class AuthService extends SimpleAuthService {
    @Override
    public void hasPermission(@NonNull Logic logic, @NonNull String... permissions) {
        /**
         * a:* -> a:a
         * b:** -> b:b:b
         * c:*:c -> c:c:c
         */
        AuthUtils.hasAntPermission(getContext(), logic, permissions);
    }
}
```

我们同样提供了测试模块`micro-test`方便用户模拟登录用户进行集成测试，很简单使用`@WithMockUser`注解即可

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

在响应式中，service层需要权限时，可以使用`AuthStepVerifier`替换掉`StepVerifier`，`AuthStepVerifier`会提供授权的上下文，当然你也可以通过`TestAuthContextHolder`获取上下文自己添加

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

`micro-auth-casdoor` 是这个鉴权模块的扩展，用于支持casdoor的token认证，你可以参考他创建自己的鉴权模块

#### Web 模块

在响应式编程中，提供 `FluentWebFilter`，方便用户在过滤器中使用权限的场景，你可以向下面一样，十分轻松的实现过滤器的配置

```java
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

> Tip: 这是对过滤器的扩展，所以不仅仅能用于鉴权，任何你希望过滤的场景，都可以使用这个过滤器

在传统的 servlet 中，一般使用拦截器对请求进行拦截，Spring 已经对 Ant Path 匹配风格做了适配，而我们鉴权这块也很简单，仅仅调用一个方法即可，所以，对于 servlet 不进行过度封装，仅提供一个 Function 工具类

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Resource
    private AuthService authService;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(FunctionHandlerInterceptor.preHandle((request, response, handler) -> {
                authService.hasLogin();
                return true;
            }))
            .addPathPatterns("/**")
            .excludePathPatterns("/", "/login");
    }
}
```

#### Jooq 扩展

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-sql-jooq</artifactId>
    <version>${last-version}</version>
</dependency>
```

为什么不使用 Mybatis? 在中国 Mybatis 无疑是使用人数最多的，但多并不代表的好，我不喜欢在 xml 中写 sql, 因为它不能很好的对类型进行校验, 而 Jooq 可以像写 java 一样写 sql, 这对我来说很有吸引力, 这个扩展是提供一些方便的工具，比如分页查询

```java
PageUtils.selectFrom(create, ADMIN_USER)
    .conditions(condition(new AdminUserRecord(user)))
    .pageable(pageable)
    .fetchPage(AdminUser.class)
```

如果你希望修改查询内容

```java
PageUtils.select(create, field("val1"), field("val2") ...)
    .from(ADMIN_USER)
    .conditions(condition(new AdminUserRecord(user)))
    .pageable(pageable)
    .fetchPage(AdminUser.class)
```

在 Jooq 中进行响应式开发，也就是使用`webflux`时, 你需要先添加一个DSLContext配置

```java
@Bean
public DSLContext dslContext(ConnectionFactory connectionFactory) {
    return DSL.using(connectionFactory);
}
```

之后，使用 `biSubscribe()` 获取并转换值，下面是一个例子

```java
PageUtils.selectFrom(create, SYSTEM_USER)
    .conditions(SYSTEM_USER.IS_DELETED.eq((byte) 0)
        .and(StringUtils.hasLength(user.getUsername()) ?
            SYSTEM_USER.USERNAME.like(user.getUsername() + "%") :
            noCondition()))
    .pageable(pageable)
    .biSubscribe((listS, countS) -> Mono.zip(
        Flux.from(listS).map(l -> l.into(SystemUser.class)).collectList(),
        Mono.from(countS).map(Record1::value1)))
    .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
```

也提供了一个帮助生成JOOQ代码的工具类，在程序中控制JOOQ代码生成比maven插件有更多的自定义空间，尤其是当项目中已经有了数据库链接配置，那么我们需要做的只是读取配置，转换成我们需要的类，这个工具类就是做这些杂事的

```java
@SpringBootTest
public class GenerateTest {

    @Resource
    DataSourceProperties properties;

    @Test
    public void generate() throws Exception {
        GenerateHelper.init(properties);
        GenerationTool.generate(new Configuration()
            .withJdbc(GenerateHelper.getJdbc())
            .withGenerator(new Generator()
                    .withDatabase(GenerateHelper.getDatabase(".*"))
                    .withTarget(GenerateHelper.getTarget("com.example.jooq"))
                    .withGenerate(new Generate()
                            .withPojos(true)
                            .withPojosAsJavaRecordClasses(true)
                            .withValidationAnnotations(true)
                            .withDaos(true))));
    }

}
```

- GenerateHelper.init(properties) 添加 spring boot 的数据源配置
- GenerateHelper.getJdbc() 获取 jdbc 配置
- GenerateHelper.getDatabase(tableNamePattern) 获取数据库配置
- GenerateHelper.getTarget(packageName) 获取生成位置的配置

#### 其他模块

其他的一些模块可能更多的是包含个人习惯的配置，如果你希望了解，可以进各个模块下，查看更详细的描述

### 一起开发

如果你希望参与到这个项目的开发，依照下面的步骤搭建好开发环境

#### 创建 casdoor

由 casdoor 提供认证和鉴权功能，下面命令行创建一个体验版的casdoor，你在线上部署时，请参考官方文档配置数据库。

```shell
docker run -p 28000:8000 -d casbin/casdoor-all-in-one
```

*开发环境demo-backend设置的端口是28000，不采用8000是考虑到，8000可能会有端口冲突*

##### 创建应用

访问 http://localhost:28000/ 添加应用，然后获取 clientId 和 clientSecret (todo 待完善)

