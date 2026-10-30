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

迁移脚本放 `src/main/resources/db/migration/`。两种命名都可用：`V1.0.0__init.sql`（`V<版本>__<描述>.sql`）或 demo 的 `V1.0.0.sql`（不带描述）；**可重复执行的字典数据用 `R__` 前缀**（如 `R__sys_permission.sql`），内容改了就按校验和重跑。

`--features db` 生成的脚本（与最新 demo 一致）：

| 脚本 | 内容 |
| --- | --- |
| `V1.0.0.sql` | `book` 表 + 3 条种子数据（含 `create_time`） |
| `V1.1.0.sql` | `sys_user` / `sys_role` / `sys_permission` / `sys_user_role` / `sys_role_permission` + `admin/admin123`、`reader/reader123`（bcrypt） |
| `V1.2.0.sql` | `sys_config`（系统配置覆盖值，配合 `config` 特性） |
| `R__sys_permission.sql` | 权限字典，脚本内容即权威列表（先 `DELETE` 再 `INSERT`） |

关联表（`sys_user_role` / `sys_role_permission`）**不建外键**，关系由应用层维护，删角色时要显式清理。

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
    // Kotlin 项目：src/main/kotlin；Java 项目：src/main/java
    private val outputDirectory: String = "src/main/kotlin"

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
- **`outputDirectory` 必须与源集一致**：Kotlin 项目写 `src/main/kotlin`，Java 项目写 `src/main/java`。jmicro demo 用的是 `src/main/java`，那是早期 Java 项目的历史遗留，Kotlin 新项目照搬会让生成代码落在错误的源集目录里（虽然能被 Gradle 编译，但与项目结构不一致，IDE 索引与后续维护都会混乱）。
- 生成产物（`*.jooq.tables.*`、`tables.daos.*`、`tables.pojos.*`）提交进仓库，`dao` 类带 `@Repository` 可直接注入。
- 用 `@Tag("db-generate")` 标记，避免 CI 中无数据库时执行；`build.gradle.kts` 可加：
  ```kotlin
  tasks.withType<Test> { useJUnitPlatform { if (project.hasProperty("ai")) excludeTags("db-generate") } }
  ```
- 若要 Java POJO 而非 Kotlin data class，改用 Lombok 生成选项并引入 `kotlin("plugin.lombok")`；`ExtendGenerator` 可提供 `fetchPage` 与表 POJO 类型。

## DAO 与分页

分页参数由 Spring Data Web 自动解析（demo 的 `common/Pageables.kt` + `common/PageResult.kt`）：

- `page` **从 0 开始**、`size` 默认 20、`sort=属性名[,asc|desc]`（pojo 驼峰字段，可重复传参做多字段排序）。
- 未指定排序时用 `Pageable.withDefaultSort(...)` 补稳定排序，否则翻页可能出现重复或遗漏记录。
- 响应统一返回 `PageResult`（`content`/`page`/`size`/`totalElements`/`totalPages`/`first`/`last`），不要直接返回 Spring Data 的 `Page`：其 JSON 结构不稳定且带 `pageable`、`sort` 等冗余字段。
- 不要开启 `spring.data.web.pageable.one-indexed-parameters`：开启后请求 `page=1` 返回的 `page` 是 0，与接口文档/前端页码错位；改 `spring.data.web.sort.sort-parameter` 时也要同步改接口文档与测试（默认就是 `sort`）。

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

## 落库改造顺序（重要）

demo 的 Service 直接用生成的 DAO：

```kotlin
@Service
class BookService(private val bookDao: BookDao) {

    fun list(): List<Book> = bookDao.findAll()

    fun get(id: Long): Book = bookDao.fetchOneById(id)
        ?: throw BaseExceptionUtils.notFound("图书不存在：$id")

    fun create(form: BookForm): Book {
        val book = Book(null, form.title, form.author, form.isbn, form.price, form.stock, LocalDateTime.now())
        bookDao.insert(book)   // 插入后回填主键
        return book
    }

    fun update(id: Long, form: BookForm): Book { /* 改字段 → bookDao.update(book) */ }

    fun delete(id: Long) {
        get(id)                // 不存在时返回 404
        bookDao.deleteById(id)
    }
}
```

这里的 `Book` 是 `*.jooq.tables.pojos.Book`（Kotlin data class）。**顺序不能反**：

1. 先 `./gradlew test --tests "*DBGenerateTest*"` 生成 `*.jooq.*`（需要有可连的数据库，Flyway 已迁移）。
2. 再把主代码（`BookService`、`UserStore`、`RoleService` 等）换成引用生成类的实现。

主代码在生成之前引用 `*.jooq.tables.daos.*` 会让 `compileKotlin` 直接失败——模板因此默认给内存实现，`db` 特性只提供迁移脚本与 `DBGenerateTest`，落库代码按 `references/auth.md` 的 demo 结构替换。

## 无数据库时的替代

新项目尚未确定持久化方案时，先用内存实现（模板 `BookService` 即 `ConcurrentHashMap`），只保留 `spring-boot-starter-validation`，待模型稳定后再引入 Flyway + jOOQ，避免早期反复重新生成代码。
