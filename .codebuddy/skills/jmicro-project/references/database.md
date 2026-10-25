# 数据库：Flyway 扩展 + jOOQ

## 依赖

```kotlin
implementation("com.jiangtj.micro:micro-business")     // Flyway 扩展在这里，Flyway 本身为 optional
implementation("com.jiangtj.micro:micro-sql-jooq")     // PageUtils / GenerateHelper
implementation("org.springframework.boot:spring-boot-starter-flyway")
implementation("org.springframework.boot:spring-boot-starter-jooq")
runtimeOnly("com.h2database:h2")                       // 或 mysql 驱动
testImplementation("org.jooq:jooq-codegen")            // 代码生成测试需要
```

`micro-business` 中 Flyway 是 `compileOnly/optional`，不引入 Flyway 依赖时 `MicroFlywayAutoConfiguration` 的 `@ConditionalOnClass(Flyway)` 不生效，模块可正常使用。

## 数据源与 Flyway 配置

```properties
spring.datasource.url=jdbc:h2:mem:demo;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=SYSTEM_USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.flyway.enabled=true
spring.flyway.locations=classpath:/db/migration
```

迁移脚本放 `src/main/resources/db/migration/V1.0.0__init.sql`（Spring Boot 4 下 `V<版本>__<描述>.sql`）。

开发环境可追加 profile：

```properties
# application-dev.properties
spring.flyway.clean-disabled=false
jmicro.flyway.clean-on-validation-error=true
spring.flyway.locations=classpath:/db/migration,classpath:/dev/db/migration
```

`jmicro.flyway.clean-on-validation-error=true` 时，`migrate` 抛 `FlywayValidateException` 会自动 `clean` 后重跑。**仅限开发/测试**。

## jOOQ 代码生成

jOOQ 需要从数据库生成表类，流程是"先迁移建表 → 再生成代码"：

```kotlin
@SpringBootTest
@Tag("db-generate")
class DBGenerateTest(@Autowired private val properties: DataSourceProperties) {

    private val packagePath: String = this::class.java.`package`.name
    private val outputDirectory: String = "src/main/java"

    @Test
    fun generate() {
        GenerateHelper.init(properties)
        GenerationTool.generate(
            Configuration()
                .withJdbc(GenerateHelper.getJdbc())
                .withGenerator(
                    Generator()
                        .withName(KotlinGenerator::class.java.name)
                        .withDatabase(GenerateHelper.getDatabase(".*")
                            .withOutputSchemaToDefault(true)
                            .withOutputCatalogToDefault(true))
                        .withTarget(Target()
                            .withPackageName("$packagePath.jooq")
                            .withDirectory(outputDirectory))
                        .withGenerate(Generate()
                            .withSpatialTypes(false)
                            .withValidationAnnotations(true)
                            .withFluentSetters(false)
                            .withPojos(true)
                            .withPojosEqualsAndHashCode(false)
                            .withPojosToString(false)
                            .withPojosAsKotlinDataClasses(true)
                            .withDaos(true)
                            .withSpringAnnotations(true)
                            .withDefaultSchema(false)
                            .withDefaultCatalog(true))
                )
        )
    }
}
```

- `DataSourceProperties` 在 Boot 4 位于 `org.springframework.boot.jdbc.autoconfigure`。
- 生成产物（`*.jooq.tables.*`、`tables.daos.*`、`tables.pojos.*`）提交进仓库，`dao` 类带 `@Repository` 可直接注入。
- 用 `@Tag("db-generate")` 标记，避免 CI 中无数据库时执行；`build.gradle.kts` 可加：
  ```kotlin
  tasks.withType<Test> { useJUnitPlatform { if (project.hasProperty("ai")) excludeTags("db-generate") } }
  ```
- 若要 Java POJO 而非 Kotlin data class，改用 Lombok 生成选项并引入 `kotlin("plugin.lombok")`；`ExtendGenerator` 可提供 `fetchPage` 与表 POJO 类型。

## DAO 与分页

生成 DAO 继承 jOOQ `DAOImpl`，`micro-sql-jooq` 的 `PageDAOImpl`/`KtDao` 额外提供 `fetchPage`：

```kotlin
val page = bookDao.fetchPage(pageable)
```

或使用 `PageUtils`：

```java
PageUtils.selectFrom(create, BOOK)
    .conditions(condition(new BookRecord(form)))
    .pageable(pageable)
    .fetchPage(Book.class);

// 列表与总数分开
List<Book> list = PageUtils.selectLimitList(create.select(BOOK).from(BOOK), pageable, condition);
int total = PageUtils.selectCount(create.selectCount().from(BOOK), condition);
```

## 无数据库时的替代

新项目尚未确定持久化方案时，先用内存实现（模板 `BookService` 即 `ConcurrentHashMap`），只保留 `spring-boot-starter-validation`，待模型稳定后再引入 Flyway + jOOQ，避免早期反复重新生成代码。
