# micro-business

jmicro 的业务能力聚合模块，用于承载通用的业务侧自动配置与扩展能力。当前内置了对 Flyway 的轻量级扩展。

## 模块特性

- 继承 Spring Boot 默认自动配置体系，通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册。
- 内置 **Flyway 扩展**（可选依赖，见下文），在校验失败时提供可选的自动清理能力，适合开发、测试环境的快速重建。

## 使用方法

### 添加依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-business</artifactId>
</dependency>
```

### Flyway 扩展（可选）

Flyway 相关依赖在 `micro-business` 中是**可选（optional）**的，模块本身不会强制传递 Flyway。
若要启用 Flyway 扩展，需在使用方自行引入 Flyway 依赖：

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-business</artifactId>
</dependency>
<!-- 可选能力：需自行引入 Flyway -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>
```

未引入 Flyway 时，`MicroFlywayAutoConfiguration` 因 `@ConditionalOnClass(Flyway.class)` 不会生效，模块可正常用于无 Flyway 的场景。

#### 配置属性

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

#### 工作机制

当 Flyway 在执行 `migrate` 时发生 `FlywayValidateException`：

- `clean-on-validation-error=false`：直接抛出异常，行为与 Spring Boot 默认一致
- `clean-on-validation-error=true`：自动执行 `clean` 后重新 `migrate`

如果你需要完全关闭 Flyway，可继续使用 Spring Boot 的配置：

```properties
spring.flyway.enabled=false
```

## 扩展指南

新增业务能力时，请在 `com.jiangtj.micro.business` 下建立子包，遵循项目约定：

- 包级 `package-info.java` 标注 `@NullMarked`
- 通过 `@AutoConfiguration` 暴露自动配置，并在 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中注册
- 可选能力使用 `compileOnly` + `runtimeOnly` 声明依赖，避免强制传递给使用者
