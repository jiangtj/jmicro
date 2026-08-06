# J Micro Common

![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-common/api)

通用工具模块，提供 JSON 处理、日期格式化、表单校验规则生成、基础校验注解以及常用工具类，支持 Java 与 Kotlin 项目。

## 目录

- [引入依赖](#引入依赖)
- [功能概览](#功能概览)
- [使用示例](#使用示例)
  - [JSON 处理](#json-处理)
  - [日期格式化](#日期格式化)
  - [表单规则生成](#表单规则生成)
  - [校验注解](#校验注解)
  - [常用工具类](#常用工具类)
  - [异常基类](#异常基类)

## 引入依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-common</artifactId>
</dependency>
```

> 建议配合 `micro-dependencies` BOM 使用，省略版本号（见[根 README](../README.md)）。

## 功能概览

- **JSON 工具**：`JsonUtils` 统一的 `JsonMapper` 封装、扩展函数 `toJson/fromJson`。
- **日期格式化**：常用 `DateTimeFormatter` 常量（`DATE_TIME`、`RFC3339` 等）。
- **表单规则生成**：`FormRuleGenerator` 根据 `jakarta.validation` 注解生成 [async-validator](https://github.com/yiminghe/async-validator) 规则。
- **校验注解**：`@MobilePhone`、`@MinLength`、`@MaxLength`。
- **异常基类**：`MicroException`、`MicroHttpException`、`MicroProblemDetailException`。
- **常用工具类**：`FileNameUtils`、`UUIDUtils`、`RandomStringUtils`、`PhoneNumberUtils`、`MapUtils`。

## 使用示例

### JSON 处理

```kotlin
val payload = mapOf("name" to "micro")
val json = JsonUtils.toJson(payload)
val back = JsonUtils.fromJson(json, Map::class.java)

val json2 = payload.toJson()
val back2: Map<String, String> = json2.fromJson()
```

如果需要替换默认的 `JsonMapper`（应用启动时由 `JMicroCommonAutoConfiguration` 初始化）：

```kotlin
val mapper = JsonMapper.builder().build()
JsonUtils.init(mapper)
```

### 日期格式化

```kotlin
val now = LocalDateTime.now()
val text = now.format(DATE_TIME)
val rfc3339 = now.atOffset(ZoneOffset.UTC).format(RFC3339)
```

### 表单规则生成

`FormRuleGenerator` 会读取 `jakarta.validation` 及 `micro-common` 自定义注解，生成前端表单校验规则。

```java
@Data
class Example {
    @NotBlank
    private String name;

    @Min(18)
    private int age;

    @MobilePhone
    private String phone;
}

Map<String, List<FormRule>> rules = FormRuleGenerator.generate(Example.class);
String json = JsonUtils.toJson(rules);
```

Kotlin 也可以直接使用泛型版本：

```kotlin
val rules = FormRuleGenerator.generate<Example>()
```

如果需要自定义规则处理器：

```kotlin
FormRuleGenerator.addHandler(CustomHandler())
```

### 校验注解

- `@MobilePhone`：支持 `String` 与 `Long` 类型的手机号校验。
- `@MinLength` / `@MaxLength`：补充字符串长度校验（可配置是否 `trim`）。

### 常用工具类

```kotlin
val filename = FileNameUtils.getRandomFileNameWithSuffix("png")
val ext = FileNameUtils.getFileExtension("avatar.png")

val shortUuid = UUIDUtils.generateBase64Compressed()
val code = RandomStringUtils.get(6)

val e164 = PhoneNumberUtils.toE164("13800000000")

val map = dto.toMap()
```

### 异常基类

- `MicroException`：通用运行时异常基类。
- `MicroHttpException`：携带 HTTP 状态码的异常。
- `MicroProblemDetailException`：携带 `title`/`detail` 的问题详情异常（供上层 starter 转换为 RFC 9457 响应）。

> 上层异常处理见 [micro-spring-boot-starter/README.md](../micro-spring-boot-starter/README.md)。
