# J Micro SQL JOOQ

![doc](https://img.shields.io/badge/document-grey.svg?logo=readme)
[![dokka](https://img.shields.io/badge/dokka-grey.svg?logo=kotlin)](https://jiangtj.com/jmicro/micro-sql-jooq/api)

提供业务层面的 JOOQ 封装：通用 DAO、分页查询、Lombok POJO 生成扩展等。相比 MyBatis，JOOQ 可以像写 Java 一样写 SQL，并具备良好的类型校验。

## 目录

- [引入依赖](#引入依赖)
- [PageUtils 分页查询](#pageutils-分页查询)
  - [Fluent API](#fluent-api)
  - [分别查询列表与总数](#分别查询列表与总数)
  - [响应式](#响应式)
- [GenerateHelper 代码生成](#generatehelper-代码生成)
- [ExtendGenerator 扩展生成器](#extendgenerator-扩展生成器)

## 引入依赖

```xml
<dependency>
    <groupId>com.jiangtj.micro</groupId>
    <artifactId>micro-sql-jooq</artifactId>
</dependency>
```

> 建议配合 `micro-dependencies` BOM 使用，省略版本号（见[根 README](../README.md)）。

## PageUtils 分页查询

### Fluent API

```java
PageUtils.selectFrom(create, ADMIN_USER)
    .conditions(condition(new AdminUserRecord(user)))
    .pageable(pageable)
    .fetchPage(AdminUser.class)
```

如果你希望修改查询内容：

```java
PageUtils.select(create, field("val1"), field("val2") /* ... */)
    .from(ADMIN_USER)
    .conditions(condition(new AdminUserRecord(user)))
    .pageable(pageable)
    .fetchPage(AdminUser.class)
```

### 分别查询列表与总数

```java
Condition condition = ...;
PageUtils.selectLimitList(create.select(table).from(table), pageable, condition);
PageUtils.selectCount(create.selectCount().from(table), condition);
```

### 响应式

首先添加一个 `DSLContext` 配置：

```java
@Bean
public DSLContext dslContext(ConnectionFactory connectionFactory) {
    return DSL.using(connectionFactory);
}
```

之后，使用 `subscribe()` 获取并转换值：

```java
public Mono<Page<AdminUser>> fetchPage() {
    return PageUtils.selectFrom(create, ADMIN_USER)
        .conditions(condition(new AdminUserRecord(user)))
        .pageable(pageable)
        .subscribe(Flux::from, Mono::from)
        .map(PageReactiveUtils.toPage(AdminUser.class));
}
```

## GenerateHelper 代码生成

帮助生成 JOOQ 代码的工具类。在程序中控制代码生成比 Maven 插件更具自定义空间——尤其当项目已有数据库链接配置时，只需读取配置即可转换为目标类，本工具类负责这些杂事：

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
                .withTarget(GenerateHelper.getTarget("com.jiangtj.platform.system.jooq"))
                .withGenerate(new Generate()
                    .withPojos(true)
                    .withPojosAsJavaRecordClasses(true)
                    .withValidationAnnotations(true)
                    .withDaos(true))));
    }
}
```

- `GenerateHelper.init(properties)`：注入 Spring Boot 数据源配置
- `GenerateHelper.getJdbc()`：获取 JDBC 配置
- `GenerateHelper.getDatabase(tableNamePattern)`：获取数据库配置
- `GenerateHelper.getTarget(packageName)`：获取生成位置配置

## ExtendGenerator 扩展生成器

扩展 JOOQ 代码生成器，提供以下功能：

- 为表定义提供获取 POJO 类型（默认 `false`）
- 为 DAO 生成类提供 `fetchPage` 方法（默认 `true`）

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
                .withName(ExtendGenerator.class.getName())
                .withDatabase(GenerateHelper.getDatabase(".*"))
                .withTarget(GenerateHelper.getTarget("com.jiangtj.platform.system.jooq"))
                .withGenerate(new Generate()
                    .withPojos(true)
                    .withPojosAsJavaRecordClasses(true)
                    .withValidationAnnotations(true)
                    .withDaos(true))));
    }
}
```

通过 `withName` 设置生成类即可。你也可以通过 `GenerateHelper` 修改默认配置；同时也提供了 `PageDAOImpl`，修改你的 DAO 类继承它即可获得相同效果。
