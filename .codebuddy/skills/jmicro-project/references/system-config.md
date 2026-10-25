# 系统配置 JMicroConfig（micro-business）

默认关闭，**必须**显式开启：

```properties
jmicro.config.enabled=true
jmicro.config.kv.site-name=JMicro
```

未开启时：`JMicroConfigService` 不注册，图片上传各 provider 也不注册。

## 三个扩展点

| 接口 | 作用 |
| --- | --- |
| `JMicroConfigLoader` | 启动时贡献默认配置项（`List<SystemItemInfo>`） |
| `JMicroConfigSaver` | 覆盖值持久化；默认 `InMemoryJMicroConfigSaver`（`@ConditionalOnMissingBean`） |
| `JMicroConfigUpdateEvent` / `JMicroConfigRefreshEvent` | 配置变更 / 刷新事件，可监听做热更新 |

## SystemItemInfo 字段

```kotlin
SystemItemInfo(
    key = "site.name",                 // 唯一键，建议与属性名对齐
    name = "站点名称",                  // 表单标题
    description = "展示在导航栏",        // 说明
    value = "JMicro",                  // 默认值（字符串；JSON 类型放紧凑 JSON 文本）
    group = SystemGroup("站点", 10),    // 分组与排序
    type = SystemItemType.TEXT.name,   // TEXT / TEXTAREA / SWITCH / SELECT / JSON / ...
    tag = listOf(PUBLIC_TAG),          // 前端可按 tag 过滤；secret 项不要打 public
    order = 0,
    secret = false,                    // true 时 getValue 正常、getAllConfig 脱敏为 ******
    valueFormatter = Function { it.trim().ifEmpty { throw IllegalArgumentException("不能为空") } },
    jsonType = mapType,                // 仅 JSON 类型：写入校验 + 生成 schema
    secretPaths = listOf("a.accessKey"), // 仅 JSON 类型：只脱敏指定路径
)
```

`valueFormatter` 在写入时执行，抛异常即拒绝写入（会转成 4xx）。

## JMicroConfigService 常用方法

| 方法 | 说明 |
| --- | --- |
| `getValue(key)` / `isTrue(key)` | 读取，优先覆盖值，未命中回退默认值（Caffeine 缓存） |
| `updateConfig(key, value)` | 校验 + 持久化 + 发布更新事件 |
| `deleteConfig(key)` | 删除覆盖值，回退默认值 |
| `getAllConfig()` | 排序后的全量视图，`secret` 脱敏、带 `formatedValue` 与 `schema` |
| `getConfigByTag(tag)` | 按 tag 过滤 |
| `refreshConfig()` | 清缓存并发布刷新事件 |
| `generateConfig(key, value)` | 生成可粘贴的 `jmicro.config.kv.*` 配置行 |
| `getObject/getList/getMap(key, type)` | JSON 类型反序列化 |
| `getJson(key)` / `getConfigField(key, "a.b")` | JSON 树 / 按路径取值（支持 `a.b` 与 `/a/b`） |
| `updateConfigField(key, path, value)` | 字段级更新 |
| `updateMapEntry` / `removeMapEntry`、`addListElement` / `updateListElement` / `removeListElement` | 维护 Map/List 结构 |

写入 JSON 类型时用 `jsonType` 校验，非法值抛 `MicroConfigException`；`secretPaths` 中的 `******` 掩码在写入时会用旧值回填。

## 示例：默认配置项

```kotlin
@Component
class AppConfigLoader : JMicroConfigLoader {
    override fun load(): List<SystemItemInfo> = listOf(
        SystemItemInfo(
            key = "app.site.name",
            name = "站点名称",
            value = "My App",
            group = SystemGroup("站点", 10),
            type = SystemItemType.TEXT.name,
            tag = listOf(PUBLIC_TAG),
            order = 0,
        ),
        SystemItemInfo(
            key = "app.site.maintenance",
            name = "维护模式",
            value = "false",
            type = SystemItemType.SWITCH.name,
            order = 1,
            valueFormatter = Function {
                it.trim().lowercase().also { v ->
                    require(v == "true" || v == "false") { "仅支持 true / false" }
                }
            },
        ),
    )
}
```

## 示例：落库的 Saver

```kotlin
@Repository
class DbConfigSaver(private val dsl: DSLContext) : JMicroConfigSaver {
    override fun fetchOneByConfigKey(key: String): String? = /* select */
    override fun findAll(): List<Pair<String, String>> = /* select all */
    override fun save(key: String, value: String) { /* upsert */ }
    override fun delete(key: String) { /* delete */ }
}
```

注册为 Bean 后自动替换内存实现（`@ConditionalOnMissingBean`）。

## 示例：管理接口

```kotlin
@HasRole("admin")
@RestController
@RequestMapping("/system/configs")
class ConfigController(private val service: JMicroConfigService) {
    @GetMapping fun list(@RequestParam(required = false) tag: String?): List<SystemItemInfo> =
        if (tag.isNullOrBlank()) service.getAllConfig() else service.getConfigByTag(tag)

    @GetMapping("/{key}") fun get(@PathVariable key: String) =
        service.getAllConfig().firstOrNull { it.key.trimKey() == key.trimKey() }
            ?: throw BaseExceptionUtils.notFound("配置项不存在：$key")

    @PutMapping("/{key}")
    fun update(@PathVariable key: String, @RequestBody form: ConfigForm): SystemItemInfo {
        service.updateConfig(key, form.value)
        return get(key)
    }

    @DeleteMapping("/{key}") fun reset(@PathVariable key: String): SystemItemInfo {
        service.deleteConfig(key); return get(key)
    }

    @PostMapping("/refresh") fun refresh() = service.refreshConfig()
}

data class ConfigForm(var value: String = "")
```

公开读取接口用 `getConfigByTag(PUBLIC_TAG)` 暴露 `public` 标记项，不要暴露 `secret` 项。

## 与 Spring 属性的分工

- 启动期必须确定的（端口、数据源、密钥）→ `application.properties` / `@ConfigurationProperties`。
- 运行期可改的（站点名、上传 provider、阈值）→ 系统配置项，读 `jmicroConfigService.getValue(...)`。
