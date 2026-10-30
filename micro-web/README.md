# J Micro Web

![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-web/api)

`micro-web` 提供 Web 层通用扩展能力，是 `micro-auth` / `micro-business` 的底层支撑：

- **拦截器扩展**：基于 Spring `HandlerInterceptor` 的通用封装（`PreHandleInterceptor` / `PostHandleInterceptor` / `AfterCompletionInterceptor` / `FunctionHandlerInterceptor`）。
- **AOP 注解扩展**：`AnnotationMethodBeforeAdvice` 等基于注解的方法增强基础设施。
- **异常基础能力**：`BaseException` / `BaseExceptionUtils`，统一异常模型，可用于 RFC 9457 Problem Details 处理。

## 引入依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-web</artifactId>
</dependency>
```

## 拦截器扩展

`PreHandleInterceptor` / `PostHandleInterceptor` / `AfterCompletionInterceptor` / `FunctionHandlerInterceptor` 基于 Spring `HandlerInterceptor` 提供函数式封装，便于按需注册前置、后置与完成阶段的处理逻辑。

> Tip: 若使用 Spring Boot 3.5+，也可直接使用官方 `@FilterRegistration`：
> `@FilterRegistration(name = "my-filter", urlPatterns = "/test/*", order = 0)`

## 异常基础能力

`BaseException` 提供统一的异常模型（携带 `reason` 与 `status`），`BaseExceptionUtils` 提供从异常中提取信息的工具方法，业务侧可基于这些能力做统一异常处理并返回符合 RFC 9457 的 Problem Details 响应体。

Spring MVC 默认不会把内置异常（参数校验失败、415、405 等）渲染成 Problem Details，需要应用侧开启（demo-backend 已开启）：

```properties
spring.mvc.problemdetails.enabled=true
# 请求参数中的日期时间与 JSON 序列化保持一致
spring.mvc.format.date-time=yyyy-MM-dd HH:mm:ss
```

## JSON 初始化

共享 JSON 行为由自动配置统一初始化（`JMicroCommonAutoConfiguration` → `JsonUtils.init(mapper)`），应用无需手动配置 `ObjectMapper`。

## 模块关系

```
micro-web  ──►  micro-auth / micro-business
   │
   └──► 提供拦截器/AOP 扩展与异常基础能力
```

> 上层用法见 [根 README](../../README.md)。
