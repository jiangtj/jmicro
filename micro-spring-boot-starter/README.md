# J Micro Spring Boot Starter

![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-spring-boot-starter/api)

`micro-spring-boot-starter` 是应用侧的「开箱即用」默认配置入口，构建于 `micro-web` 之上，提供：

- **Web 过滤器默认启用**：引入本 starter 后，`micro-web` 的 `FluentWebFilter` 自动装配（无需手动 `@EnableFluentWebFilter`）。
- **统一异常处理**：基于 `micro-web` 的 `BaseException` 基础能力，对异常做统一处理，返回符合 **RFC 9457 Problem Details** 的响应体。
- **Servlet / WebFlux 双栈支持**：分别由 `ServletExceptionAutoConfiguration` 与 `ReactiveExceptionAutoConfiguration` 注册，按技术栈自动生效。

## 引入依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-spring-boot-starter</artifactId>
</dependency>
```

## 自动配置

通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册：

```
com.jiangtj.micro.spring.boot.reactive.ReactiveExceptionAutoConfiguration
com.jiangtj.micro.spring.boot.servlet.ServletExceptionAutoConfiguration
```

对应行为：

| 技术栈 | 注册类 | 说明 |
| --- | --- | --- |
| Servlet | `ServletExceptionAutoConfiguration` | 通过 `BaseExceptionFilter` / `BaseExceptionResolver` 拦截异常并转换为 Problem Details |
| WebFlux | `ReactiveExceptionAutoConfiguration` | 通过 `BaseExceptionHandler` 做响应式异常处理 |

## 异常响应示例

当业务抛出 `BaseException` 时，框架返回标准 Problem Details 结构（HTTP 状态 + `application/problem+json`）：

```json
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "需要登录"
}
```

## 模块关系

```
micro-web
   │  BaseException / FluentWebFilter
   ▼
micro-spring-boot-starter  ──►  应用默认异常处理 + 过滤器自动装配
   │
   ├──► micro-auth      (鉴权)
   └──► micro-business  (业务聚合)
```

> 上层用法见 [根 README](../../README.md)。
