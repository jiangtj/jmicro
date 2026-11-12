# 数据库：Flyway 扩展 + jOOQ

## 依赖

```kotlin
implementation("com.jiangtj.micro:micro-business")     // Flyway 扩展在这里，Flyway 本身为 optional
implementation("com.jiangtj.micro:micro-sql-jooq")     // PageUtils / GenerateHelper（已 api spring-boot-data-commons，提供 Pageable 解析）
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
| `R__sys_permission.sql` | 权限字典，脚本内容即权威列表（先 `DELETE` 再 `INSERT`）；含分组节点 `book`（不带 `:`），仅供前端渲染权限树，后端鉴权仍用 `book:read`/`book:write` |

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
    private val outputDirectory: String = detectOutputDirectory()

    /**
     * 自动识别生成目录：优先选择已存在当前包路径的源码目录，其次选择存在的 src/main/java 或 src/main/kotlin
     */
    private fun detectOutputDirectory(): String {
        val projectDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        val packageDir = packagePath.replace('.', File.separatorChar)
        val candidates = listOf("src/main/java", "src/main/kotlin")
        return candidates.firstOrNull { Files.isDirectory(projectDir.resolve(it).resolve(packageDir)) }
            ?: candidates.firstOrNull { Files.isDirectory(projectDir.resolve(it)) }
            ?: "src/main/java"
    }

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
- **生成目录必须与源集一致**：demo 的 `detectOutputDirectory()` 会优先选已存在当前包路径的目录，其次回落 `src/main/java` → `src/main/kotlin`；Kotlin 新项目自然落到 `src/main/kotlin`，不要硬编码成 demo 早期的 `src/main/java`。
- 生成产物（`jooq/tables`、`jooq/tables.daos`、`jooq/tables.pojos`）提交进仓库，生成的 DAO 类带 `@Repository` 可直接注入。**生成目录整块会被重新生成覆盖**，自定义 SQL 不要写进去（见下文 `dao` 目录约定）。
- 用 `@Tag("db-generate")` 标记，避免 CI 中无数据库时执行；`build.gradle.kts` 可加：
  ```kotlin
  tasks.withType<Test> { useJUnitPlatform { if (project.hasProperty("ai")) excludeTags("db-generate") } }
  ```
- 若要 Java POJO 而非 Kotlin data class，改用 Lombok 生成选项并引入 `kotlin("plugin.lombok")`；`ExtendGenerator` 可提供 `fetchPage` 与表 POJO 类型。

## DAO 与分页

分页参数由 Spring Data Web 自动解析（demo 的 `common/Pageables.kt`）：

- 页码与排序参数名可定制：demo 配置了 `spring.data.web.pageable.one-indexed-parameters=true`（`page` **从 1 开始**）和 `spring.data.web.sort.sort-parameter=sortQ`（排序参数名为 `sortQ=属性名[,asc|desc]`，pojo 驼峰字段，可重复传参做多字段排序）；不配置时 Spring Data 默认 `page` 从 0 开始、参数名为 `sort`。
- `one-indexed-parameters` 只影响请求解析，响应中的 `page.number` 仍从 0 开始（请求 `page=1` 对应 `page.number=0`）。
- 未指定排序时用 `Pageable.withDefaultSort(...)` 补稳定排序，否则翻页可能出现重复或遗漏记录。
- 接口直接返回 Spring Data `Page`/`PageImpl`，不包一层自定义 DTO；必须配置 `spring.data.web.pageable.serialization-mode=VIA_DTO`，响应才是稳定的 `{content, page:{size, number, totalElements, totalPages}}`（默认 `DIRECT` 会带 `pageable`、`sort` 等冗余字段且结构不稳定）。
- 非数据库来源的数据（如系统配置项）用 `List<T>.toPage(pageable)` 在内存切片；若开启 `spring.data.web.pageable.one-indexed-parameters`，接口文档需同时写明"请求 `page` 从 1 开始、响应 `page.number` 从 0 开始"，避免前端页码错位。

### 分页 + 过滤的写法（demo 的 `BookService.page`）

`BookQuery` 的属性名与 jOOQ 生成的 pojo 对齐，等值条件交给 `conditionByExample`，
模糊/区间条件把对应属性作为忽略字段、再用 `and` 追加：

```kotlin
// Controller
@HasPermission("book:read")
@GetMapping("/page")
fun page(pageable: Pageable, @ModelAttribute query: BookQuery): Page<Book> =
    bookService.page(pageable, query)

// Service
fun page(pageable: Pageable, query: BookQuery = BookQuery()): Page<Book> {
    val table = BookTable.BOOK
    return bookDao.fetchPage()
        .conditionByExample(query, table.TITLE, table.PRICE)   // title/price 单独处理，故忽略
        .and(query.title?.trim()?.takeIf { it.isNotEmpty() }?.let { table.TITLE.containsIgnoreCase(it) })
        .and(query.minPrice?.let { table.PRICE.ge(it) })
        .and(query.maxPrice?.let { table.PRICE.le(it) })
        .withPageable(pageable)
        .fetchPage()
}
```

- 列表端点统一为 `GET .../page`：demo 的 Controller 不再暴露无分页的 `GET /books`（Service 的 `list()` 只留给测试/内部使用）。
- `conditionByExample(query, vararg ignore)` 按属性名生成等值条件，模糊/区间查询用到的属性必须放进忽略列表。
- `common/JooqPageables.kt` 的 `PageableStep.withPageable(pageable)` 把 Spring Data `Pageable` 转成 jOOQ 分页（`SortPropertyStyle.SNAKE_LOWER`，补默认主键倒序）。
- 关联数据用批量查询再 `page.map {}` 拼装，避免 N+1：用户列表用 `SysUserRepository.findRoleIdsByUserIds(ids)`，角色列表用 `SysRoleRepository.findPermissionCodesByRoleIds(ids)`。

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

## `dao` 目录：自定义 SQL 的默认位置

生成的 DAO 只覆盖单表 CRUD（`fetchOneById`/`findAll`/`insert`/`update`/`deleteById` + `fetchPage`）。**当生成的 DAO 满足不了需求、需要用 `DSLContext` 构建 SQL 时，优先在项目的 `dao/` 包下新建 `XxxRepository` 补充方法，而不是改生成代码（下一次 `DBGenerateTest` 会整体覆盖）或在 Controller 里直接拼 SQL。**

适合放进 `dao/` 的场景：

- 关联查询（按用户查角色、按角色查权限）与跨表 join；
- 批量 `in` 查询（列表页一次取回关联数据，避免 N+1）；
- 关联表"先删后插"整体覆盖、删除主表前显式清理关联；
- `conditionByExample` + `withPageable` 表达不了的复杂条件、`group by`/`having`、子查询、聚合统计。

约定：

- 命名 `XxxRepository`，类上 `@Repository`，构造函数注入 `DSLContext`；不要在 Controller 里注入 `DSLContext` 或拼 SQL。
- 方法名体现语义与批量性（`findRoleIdsByUserIds`、`replacePermissions`、`deleteRelations`）；对外返回 pojo 或基础类型，不返回 jOOQ `Record`/`Result`，避免生成类泄漏到 Service/Controller。
- 只做数据访问：事务与业务异常留在 Service 层（多表写入由 Service 的 `@Transactional` 编排）。
- 与生成的 DAO 混用：单表 CRUD 继续用 `bookDao`/`sysUserDao`，Repository 只补缺，别把同一查询写两遍。

```kotlin
// dao/SysUserRepository.kt（demo 写法）
@Repository
class SysUserRepository(private val dsl: DSLContext) {

    fun findRoleIdsByUserId(userId: Long): List<Long> =
        dsl.select(SysUserRole.SYS_USER_ROLE.ROLE_ID)
            .from(SysUserRole.SYS_USER_ROLE)
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.eq(userId))
            .orderBy(SysUserRole.SYS_USER_ROLE.ROLE_ID)
            .fetch(SysUserRole.SYS_USER_ROLE.ROLE_ID)
            .filterNotNull()

    // 批量版：一次 in 查询后分组，避免列表页 N+1
    fun findRoleIdsByUserIds(userIds: Collection<Long>): Map<Long, List<Long>> =
        if (userIds.isEmpty()) emptyMap()
        else dsl.select(SysUserRole.SYS_USER_ROLE.USER_ID, SysUserRole.SYS_USER_ROLE.ROLE_ID)
            .from(SysUserRole.SYS_USER_ROLE)
            .where(SysUserRole.SYS_USER_ROLE.USER_ID.`in`(userIds))
            .orderBy(SysUserRole.SYS_USER_ROLE.ROLE_ID)
            .fetch()
            .mapNotNull { r -> r.value1()?.let { uid -> r.value2()?.let { rid -> uid to rid } } }
            .groupBy({ it.first }, { it.second })

    fun replaceRoles(userId: Long, roleIds: Collection<Long>) { /* deleteFrom 旧绑定 → insertInto 新绑定 */ }

    fun deleteRelations(userId: Long) { /* 关联表无外键，删除用户前显式清理 */ }
}

// Service 混用：单表走生成 DAO，关联查询走 Repository
@Service
class UserService(
    private val sysUserDao: SysUserDao,
    private val userRepository: SysUserRepository,
) { /* ... */ }
```

上例的表类来自生成包（如 `<pkg>.jooq.tables.SysUserRole`），`SysUserDao` 是 `tables.daos` 下的生成 DAO。
自定义分页查询同样返回 Spring Data `Page`（保持 `/page` 接口契约一致）：仓库方法接收 `Pageable`，用 `common/JooqPageables.kt` 的 `withPageable(pageable)` 或 `PageUtils` 转换。

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
