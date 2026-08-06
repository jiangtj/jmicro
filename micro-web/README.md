# J Micro Web

![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-web/api)

`micro-web` 提供 Web 层通用扩展能力，是 `micro-spring-boot-starter` 与 `micro-auth` 的底层支撑：

- **`FluentWebFilter`**：响应式（WebFlux）下以流式 API 声明过滤器链，支持按路径分组与嵌套。
- **过滤器/拦截器扩展**：基于 Spring `WebFilter` 与 `HandlerInterceptor` 的通用封装。
- **异常基础能力**：`BaseException` / `BaseExceptionUtils`，统一异常模型，供上层 starter 做 RFC 9457 Problem Details 处理。

## 引入依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-web</artifactId>
</dependency>
```

## FluentWebFilter

响应式开发中，`FluentWebFilter` 让过滤器配置变得轻松。通过 `FluentWebFilter.create()` 构建，支持：

- `.exclude(...)`：声明放行路径。
- `.action(...)`：注册根级过滤器动作（返回 `Mono<Void>`）。
- `.path(...).action(...)`：为指定路径组注册专属动作，可进一步嵌套。

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

> 这本质是对过滤器的扩展，不只可用于鉴权——任何希望拦截处理的场景都可以使用。

> Tip: 若使用 Spring Boot 3.5+，也可直接使用官方 `@FilterRegistration`：
> `@FilterRegistration(name = "my-filter", urlPatterns = "/test/*", order = 0)`

## 异常基础能力

`BaseException` 提供统一的异常模型（携带 `reason` 与 `status`），`BaseExceptionUtils` 提供从异常中提取信息的工具方法。上层 `micro-spring-boot-starter` 会基于这些能力做统一异常处理，并返回符合 RFC 9457 的 Problem Details 响应体。

## JSON 初始化

共享 JSON 行为由自动配置统一初始化（`JMicroCommonAutoConfiguration` → `JsonUtils.init(mapper)`），应用无需手动配置 `ObjectMapper`。

## 模块关系

```
micro-web  ──►  micro-spring-boot-starter  ──►  micro-auth / micro-business
   │
   └──► 提供 FluentWebFilter 与异常基础能力
```

> 上层用法见 [根 README](../../README.md)。
