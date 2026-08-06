# J Micro Test

![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-test/api)

`micro-test` 为模块提供集成测试支持，是与 `micro-auth` 配合的测试工具集。

## 引入依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-test</artifactId>
    <scope>test</scope>
</dependency>
```

## @JMicroTest

组合注解，等价于 `@SpringBootTest` + `JMicroExtension` + 测试配置（启用 `testcase` profile），用于快速编写集成测试：

```java
@JMicroTest
class ExampleTest {
    // ...
}
```

## 基于注解的模拟用户

在测试类或方法上通过注解注入模拟鉴权上下文，无需真实登录：

| 注解 | 说明 |
| --- | --- |
| `@WithMockUser` | 模拟完整用户（`subject` / `roles` / `permissions`） |
| `@WithMockSubject` | 仅模拟 subject（身份） |
| `@WithMockRole` | 模拟角色 |
| `@WithMockPermission` | 模拟权限 |

```java
@JMicroTest
class AuthTest {

    @Test
    @WithMockUser(subject = "1", roles = {"admin"}, permissions = {"user:read"})
    void shouldPassWhenHasPermission() {
        // 此时鉴权上下文已包含 admin 角色与 user:read 权限
    }
}
```

> 这些注解最终由 `TestAnnotationConverterFactory` 转换为 `AuthContext`，通过 `TestAuthContextHolder` 在测试线程内生效。

## 响应式测试：AuthStepVerifier

在 WebFlux 下，用 `AuthStepVerifier` 替代 `StepVerifier`，它会自动把当前模拟 `AuthContext` 写入 Reactor 上下文：

```java
AuthStepVerifier.create(someMono)
    .expectNextMatches(...)
    .verifyComplete();
```

## Problem Details 断言

提供 `ProblemDetailConsumer` / `ProblemDetailMvcConsumer`，用于在测试中便捷断言 RFC 9457 异常响应：

```java
// 示例：断言返回 401 且 detail 匹配
ProblemDetailMvcConsumer.expectProblemDetail(401, "需要登录");
```

## 模块关系

```
micro-auth  ──►  提供 AuthContext / AuthService
    │
    ▼
micro-test  ──►  @JMicroTest + @WithMockUser + AuthStepVerifier
```

> 鉴权用法见 [micro-auth/README.md](../micro-auth/README.md)。
