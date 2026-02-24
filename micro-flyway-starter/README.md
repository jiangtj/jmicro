# micro-flyway-starter

一个针对 Flyway 的轻量级 Spring Boot Starter，用于在迁移校验失败时提供可选的自动清理能力，适合开发、测试环境的快速重建。

## 功能特性

- 继承 Spring Boot 默认 Flyway 自动配置
- 迁移校验失败时可选执行 `clean + migrate`
- 通过简单的配置开关控制行为

## 使用方法

### 添加依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-flyway-starter</artifactId>
</dependency>
```

### 配置属性

在 `application.properties` 或 `application.yml` 中添加配置：

```properties
# 是否在校验失败后自动清理并重新迁移
micro.flyway.clean-on-validation-error=true
```

```yaml
micro:
  flyway:
    clean-on-validation-error: true
```

`clean-on-validation-error` 默认为 `false`。建议仅在开发或测试环境启用该能力，避免误删生产数据。

## 工作机制

当 Flyway 在执行 `migrate` 时发生 `FlywayValidateException`：

- `clean-on-validation-error=false`：直接抛出异常，行为与 Spring Boot 默认一致
- `clean-on-validation-error=true`：自动执行 `clean` 后重新 `migrate`

如果你需要完全关闭 Flyway，可继续使用 Spring Boot 的配置：

```properties
spring.flyway.enabled=false
```
