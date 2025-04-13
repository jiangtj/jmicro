# Micro Auth

当你需要对你的 Spring Boot 应用进行权限认证和鉴权时，你可能会选择一个开源框架，比如 Spring 家族的 Spring Security 或者 Apache Shiro 又或是 SaToken 等，你需要根据他们的文档一步一步的搭建，然而假如你未选择框架，从零搭建鉴权模块，你会发现大部分情况下一个 filter 足够了。为什么？那些框架真的有必要使用么？或许我们需要回过头来重新考虑，这便是我设计这个授权模块的原因，做更少的事，仅仅是对 filter 的扩展，然后提供我们熟知的注解，这便是这个模块做的所有的事

## 如何使用

第一步，添加 maven 依赖到你的项目中

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-auth</artifactId>
    <version>${last-version}</version>
</dependency>
```

第二步，配置转换器，转换你的请求到 AuthContext，下面是一个例子，使用 jjwt 解析 bearer token

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

第三步，现在你可以在任意地方，使用我们的框架来进行权限控制了，我们提供了授权类(`AuthService`/`AuthReactiveService`)，允许你在过滤器或者业务代码中使用，除此外还有注解(`@HasLogin` `@HasRole` `@HasPermission`)，允许你控制方法以及类级别的权限

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

### 处理授权上下文

一个很常见的情况，我们的认证与授权过程可能是分开的，比如 `casdoor` 模块中那样，我们需要在认证之后，完善授权信息，所以 `AuthContextHandler` 就来了

```java
@Order(Number)
@Component
public class ProvideRoleHandler implements AuthContextHandler{
    @Override
    public void handle(AuthContext ctx) {
        // 获取你的角色
        ctx.setAuthorization(Authorization.create(roles));
    }
}
```

### 匹配方式

默认授权的匹配规则是判断提供的 `key` 是否在授权上下文中存在，当然针对 `permission` 也提供了一个 `ant-style` 权限匹配规则，添加 `jmicro.auth.permission-match=ant` 配置选项即可，除此之外你可以覆盖默认的授权类，实现任意的鉴权方式(`AuthUtils` 可以帮助你简化判断权限)

```java
import com.jiangtj.micro.auth.servlet.DefaultAuthService;

@Service
public class AuthService extends DefaultAuthService {
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

### 多账号体系

我们的 `AuthContextConverter` 返回值是可以为 `Null`，如果返回 `Null`，那么会继续使用下一个转换器转换授权上下文，所以很容易实现多体系，你只需多个转换器，处理各自的授权上下文即可，如果不支持的那么返回 `Null` 让其他的处理。我们建议通过 `Subject` 的 `issuer` 和 `type` 来区分，比如 `casdoor` 模块中，`issuer` 是授权服务器地址，`type` 是固定的 `casdoor`

### 自定义注解

我们的注解也有很高的扩展性，你可以自定义自己的注解，通过组合注解的方式

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HasRole(value = {"admin", "user"}, logic = Logic.OR)
public @interface HasAdminOrUser {
}
// 那么 @HasAdminOrUser 等价于 @HasRole(value = {"admin", "user"}, logic = Logic.OR)
```

也可以通过我们提供的注解切面类`AnnotationMethodBeforeAdvice` `ReactiveAnnotationMethodBeforeAdvice` 和 `AnnotationPointcut`（不仅仅可以用于鉴权哦），下面是我们的 `@HasRole` 的实现过程

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

## 测试

我们同样提供了测试模块`micro-test`方便用户模拟用户进行集成测试，很简单使用`@WithMockUser`注解即可（或者`@WithMockSubject` `@WithMockRole` `@WithMockPermission`）

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
