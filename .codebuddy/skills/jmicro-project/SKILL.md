---
name: jmicro-project
description: 从零创建基于 JMicro（com.jiangtj.micro）的 Spring Boot 4 后端项目，并把 micro-auth 轻量鉴权、micro-business 系统配置/图片上传/Flyway 扩展、micro-sql-jooq 分页与代码生成、micro-test 集成测试接入项目。当用户要求新建或初始化一个 jmicro 项目、给现有 Spring Boot 项目接入 jmicro 登录鉴权/系统配置/图片上传，或询问「用 jmicro 怎么搭后端」「jmicro 新项目怎么写」时使用。
---

# JMicro 项目脚手架

## 用途

以 jmicro 官方 demo 后端为参考实现（等价结构已内置在 `assets/template`），生成一个可运行、可测试的 JMicro 后端项目，并按需接入各模块能力。
技能自带项目模板与 Gradle wrapper，**无需克隆 jmicro 仓库**：依赖来自 Maven Central，所有 API 细节以本 skill 的 `references/` 为准。不要凭记忆编造类名、属性名或版本号。

## 前置确认

1. 版本必须对齐（不匹配会导致依赖解析失败或自动配置不生效）：jmicro `0.4.0`、Spring Boot `4.1.1`、Kotlin `2.4.20`、Java 21。
   - 本技能的模板与 references 对应 `0.4.0`；改用更旧版本时须自行核对 API（0.3.x 可能没有 `BCryptUtils`、`com.jiangtj.micro.common.schema` 等模板用到的能力）。
   - 需要确认是否有更新版本时，读 Maven Central 元数据 `https://repo1.maven.org/maven2/com/jiangtj/micro/micro-dependencies/maven-metadata.xml`（脚本 `--latest` 也是读它）。
   - 用户指定版本则以用户为准。
2. 不需要检查本地 Maven 仓库。构建若报 `Could not find com.jiangtj.micro:micro-*:<version>`，说明该版本尚未发布到 Maven Central：改用已发布版本；确需使用未发布版本时加 `--local-repo`（写入 `mavenLocal()`），并要求用户先 `publishToMavenLocal`。

## 步骤 1：确定参数

未明确时使用默认值并直接开始，不要反复追问：

| 参数 | 说明 | 默认 |
| --- | --- | --- |
| `--path` | 新项目目录 | 当前工作区下的 `<项目名>` |
| `--package` | 基础包名 | `com.example.app` |
| `--name` | 项目名（kebab-case，用于 `spring.application.name` 与 H2 库名） | 目录名 |
| `--port` | `server.port` | `8080` |
| `--features` | 逗号分隔，取值 `db`、`config`、`pic` | 空（仅 Web + 鉴权内核） |

`--features` 语义：

| 取值 | 追加内容 | 参考 |
| --- | --- | --- |
| `db` | Flyway + jOOQ + H2、jOOQ 代码生成测试、迁移脚本目录 | `references/database.md` |
| `config` | `micro-business`、`jmicro.config.enabled=true`、系统配置 Loader 示例 | `references/system-config.md` |
| `pic` | `micro-business`、`jmicro.config.enabled=true`、`PicUploadController` | `references/pic-upload.md` |

## 步骤 2：生成骨架

优先用脚本（复制模板 + 替换占位符 + 按 feature 裁剪 `#if` 块 + 安装内置 Gradle wrapper）：

```
python scripts/init_project.py --path <目录> --package <包名> --name <项目名> --port <端口> --features db,config
```

可选参数：`--latest`（从 Maven Central 解析最新发布版本）、`--local-repo`（额外启用 `mavenLocal()`）、`--jmicro-version/--spring-boot-version/--kotlin-version`、`--no-wrapper`、`--force`。

无 Python 时，手工把 `assets/template/base` 与 `assets/template/features/<feature>` 复制到目标目录（wrapper 从 `assets/gradle-wrapper/` 取），替换下列占位符，并删除未启用 feature 的 `#if <feature>` 块：

| 占位符 | 含义 |
| --- | --- |
| `__PKG__` | 基础包名，如 `com.example.app` |
| `__PKG_PATH__` | 包路径，如 `com/example/app` |
| `__APP_CLASS__` | 应用主类前缀，如 `Shop`（`ShopApplication.kt`） |
| `__PROJECT_NAME__` | 项目名（kebab-case） |
| `__GROUP__` | Maven groupId（默认取包名去掉最后一段） |
| `__PORT__` | `server.port` |
| `__JMICRO_VERSION__`、`__SPRING_BOOT_VERSION__`、`__KOTLIN_VERSION__` | 见前置确认 |

生成后目录结构：

```
<项目>/
├── build.gradle.kts            # platform(BOM) + Boot 4 starter
├── settings.gradle.kts
├── gradle.properties
├── gradlew / gradle/wrapper/   # 技能内置，无需联网安装
└── src/main/kotlin/<pkg>/
    ├── <App>Application.kt
    ├── auth/                   # JwtProperties / JwtService / AuthContextConverter / UserStore
    ├── controller/             # AuthController(login,me) / BookController
    ├── book/                   # BookForm / BookService（内存实现，替换为真实持久层）
    └── common/                 # ApiExceptionHandler / WebConfig(CORS)
```

## 步骤 3：必做接线（缺一不可）

1. **必须自己提供 `AuthContextConverter` Bean**。`AuthContextFactory` 不做任何兜底：没有转换器时所有请求都是未登录。模板中的 `auth/JwtAuthContextConverter.kt` 是最小范式，契约见 `references/auth.md`。
2. **注册 `BaseException` → RFC 9457 ProblemDetail 的转换**。模板 `common/ApiExceptionHandler.kt` 继承 `ResponseEntityExceptionHandler` 并把 `RuntimeException` 交给 `BaseException.toProblemDetail()`，否则 `@HasLogin` 拒绝时拿不到 401 响应体。
3. **不要写** `@EnableXxx`、`ObjectMapper`/`JsonMapper` 配置、鉴权拦截器注册——自动配置已处理（`micro-web` 初始化 `JsonUtils`，`micro-auth` 注册 `@HasLogin/@HasSubject/@HasRole/@HasPermission` 四个 Advisor）。
4. 主类放在基础包根（`@SpringBootApplication` 扫描所有子包），Kotlin 组件类需要 `kotlin("plugin.spring")` 插件才能被 AOP 代理。

## 步骤 4：接入可选能力

按 `--features` 阅读对应 reference 后再改代码，不要凭印象添加属性：

- `db` → `references/database.md`（Flyway 迁移目录、jOOQ 代码生成、`PageUtils` 分页）
- `config` → `references/system-config.md`（`JMicroConfigLoader`/`Saver`、`SystemItemInfo`、`JMicroConfigService`）
- `pic` → `references/pic-upload.md`（`PicUploadService`、各 provider 配置项、`jmicro.config.enabled` 前置）
- 测试 → `references/testing.md`；模块坐标与自动配置全表 → `references/modules.md`

## 步骤 5：验证（必须真实执行并报告结果）

1. `./gradlew compileKotlin` 通过。
2. `./gradlew test` 通过（模板自带 `AuthFlowTest`，覆盖登录、401、403、JSON Schema）。
3. `./gradlew bootRun` 启动无异常后（模板内置账号 `admin/admin123`、`reader/reader123`）：
   - `POST /login` 取 token；
   - 不带 token 访问受保护接口应返回 401 且响应体为 ProblemDetail；
   - 带 token 访问成功；
   - `reader` 删除接口返回 403。
4. 任一失败按 `references/pitfalls.md` 排查；仍失败时如实报告错误输出，不要声称已通过。

## 常见失败速查

| 现象 | 原因 |
| --- | --- |
| 所有请求都未登录 / 401 | 缺少 `AuthContextConverter`，或返回 `AuthContext.unLogin()` 中断了转换链 |
| 403 但账号权限看起来正确 | `jmicro.auth.permission-match` 为默认 `Simple` 时通配符不生效，用 `ant` |
| 401/403 无响应体 | 未注册 `BaseException` 的 ControllerAdvice |
| `jmicro.config.enabled` 不生效 | 默认 `false`，必须显式开启；图片上传、provider 配置都依赖它 |
| 找不到 `spring-boot-starter-web` | Boot 4 改名为 `spring-boot-starter-webmvc` |
| jOOQ 生成的类不存在 | 生成代码需先跑 `db-generate` 标签的测试（需要可连的数据库） |
| `Could not find com.jiangtj.micro:micro-*` | 该版本未发布到 Maven Central；改用已发布版本，或 `--local-repo` + 本地发布 |

## 参考文件

- `references/modules.md` — 模块职责、Maven 坐标、自动配置清单、必配属性
- `references/auth.md` — 认证上下文契约、注解、`AuthService`、JWT 登录实现
- `references/database.md` — Flyway 扩展、jOOQ 生成与分页
- `references/system-config.md` — 系统配置能力与表单类型
- `references/pic-upload.md` — 图片上传与各 provider 配置
- `references/testing.md` — `@JMicroTest`、`@WithMockUser`、ProblemDetail 断言
- `references/pitfalls.md` — 易错点与排查顺序
