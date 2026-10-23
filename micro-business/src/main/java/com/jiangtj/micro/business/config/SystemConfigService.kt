package com.jiangtj.micro.business.config

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.jiangtj.micro.common.JsonUtils
import com.jiangtj.micro.common.schema.JsonSchemaGenerator
import com.jiangtj.micro.web.copyTo
import org.springframework.context.ApplicationEventPublisher
import tools.jackson.databind.JsonNode

/**
 * 系统配置服务
 *
 * 配置值统一以 `String` 形式持久化到 [SystemConfigSaver]，因此对象、集合、`Map`
 * 等嵌套结构使用 [SystemItemType.JSON] 类型，值为 JSON 文本。
 *
 * 注意：[updateConfigField]、[updateMapEntry]、[updateListElement] 等字段级写入是
 * 读-改-写语义，多实例部署下存在并发覆盖风险，需要由 `SystemConfigSaver` 提供
 * 版本号或锁来保证一致性。
 */
class SystemConfigService(
    loaders: List<SystemConfigLoader>,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val systemConfigProperties: SystemConfigProperties,
    private val systemConfigSaver: SystemConfigSaver,
) {

    private val defaultConfig = mutableMapOf<String, SystemItemInfo>()

    val cache: Cache<String, String> = Caffeine.newBuilder().build()

    init {
        loaders.forEach { loader ->
            loader.load().forEach {
                defaultConfig[it.key.trimKey()] = it
            }
            if (systemConfigProperties.enabled) {
                systemConfigProperties.kv.forEach { (k, v) ->
                    val item = defaultConfig[k.trimKey()]
                    if (item != null) {
                        item.value = v
                    }
                }
            }
        }
    }

    fun getValue(key: Enum<*>): String {
        return getValue(key.name)
    }

    fun getValue(key: String): String {
        return cache.get(key) {
            val config = systemConfigSaver.fetchOneByConfigKey(key)
            if (config != null) {
                return@get config
            } else {
                defaultConfig[key.trimKey()]?.value ?: ""
            }
        }
    }

    // ---- 结构化配置读取 ----

    /**
     * 将 [SystemItemType.JSON] 配置项读取为指定类型的对象，值不合法时返回 `null`。
     */
    fun <T> getObject(key: String, type: Class<T>): T? {
        return JsonUtils.fromJson(getValue(key), type)
    }

    /**
     * 将 [SystemItemType.JSON] 配置项读取为集合，值不合法或为空时返回空集合。
     */
    fun <T> getList(key: String, type: Class<T>): List<T> {
        val raw = getValue(key)
        if (raw.isBlank()) {
            return emptyList()
        }
        return JsonUtils.getListFromJson(raw, type)?.toList() ?: emptyList()
    }

    /**
     * 将 [SystemItemType.JSON] 配置项读取为 `Map<String, V>`，值不合法或为空时返回空集合。
     */
    fun <V> getMap(key: String, valueType: Class<V>): Map<String, V> {
        val raw = getValue(key)
        if (raw.isBlank()) {
            return emptyMap()
        }
        val javaType = JsonUtils.jsonMapper.typeFactory
            .constructMapType(LinkedHashMap::class.java, String::class.java, valueType)
        return JsonUtils.jsonMapper.readValue(raw, javaType)
    }

    /**
     * 将 [SystemItemType.JSON] 配置项读取为 JSON 树，供接口层直接透传给前端。
     */
    fun getJson(key: String): JsonNode? {
        val raw = getValue(key)
        if (raw.isBlank()) {
            return null
        }
        return runCatching { JsonUtils.jsonMapper.readTree(raw) }.getOrNull()
    }

    /**
     * 读取 JSON 配置项下的某个字段，[path] 支持 `a.b.c` 或 JSON Pointer `/a/b/c`。
     */
    fun getConfigField(key: String, path: String): Any? = getConfigField(key, splitPath(path))

    /**
     * 读取 JSON 配置项下的某个字段，[path] 为按层拆分的字段路径。
     */
    fun getConfigField(key: String, path: List<String>): Any? {
        val root = toJsonMap(getValue(key)) ?: return null
        return getAtPath(root, path)
    }

    fun getMapEntry(key: String, entryKey: String): Any? = getConfigField(key, listOf(entryKey))

    fun getListElement(key: String, index: Int): Any? {
        return toJsonList(getValue(key))?.getOrNull(index)
    }

    // ---- 结构化配置写入 ----

    /**
     * 更新 JSON 配置项下的某个字段，[path] 支持 `a.b.c` 或 JSON Pointer `/a/b/c`。
     */
    fun updateConfigField(key: String, path: String, value: Any?) {
        updateConfigField(key, splitPath(path), value)
    }

    /**
     * 更新 JSON 配置项下的某个字段，[path] 为按层拆分的字段路径。
     */
    fun updateConfigField(key: String, path: List<String>, value: Any?) {
        requireJsonItem(key)
        if (path.isEmpty()) {
            throw MicroConfigException("字段路径不能为空")
        }
        val root = toJsonMap(getValue(key)) ?: LinkedHashMap()
        setAtPath(root, path, toPlain(value))
        updateConfig(key, JsonUtils.jsonMapper.writeValueAsString(root))
    }

    fun updateMapEntry(key: String, entryKey: String, value: Any?) {
        updateConfigField(key, listOf(entryKey), value)
    }

    /**
     * 删除 JSON 配置项（`Map` 结构）中的某个 entry。
     */
    fun removeMapEntry(key: String, entryKey: String) {
        requireJsonItem(key)
        val root = toJsonMap(getValue(key)) ?: return
        if (!root.containsKey(entryKey)) {
            return
        }
        root.remove(entryKey)
        updateConfig(key, JsonUtils.jsonMapper.writeValueAsString(root))
    }

    fun updateListElement(key: String, index: Int, value: Any?) {
        requireJsonItem(key)
        val list = toJsonList(getValue(key)) ?: mutableListOf()
        if (index !in list.indices) {
            throw MicroConfigException("配置项值不合法: 索引越界 $index")
        }
        list[index] = toPlain(value)
        updateConfig(key, JsonUtils.jsonMapper.writeValueAsString(list))
    }

    /**
     * 向 JSON 配置项（数组结构）添加元素，[index] 小于 0 或超出范围时追加到末尾。
     */
    fun addListElement(key: String, value: Any?, index: Int = -1) {
        requireJsonItem(key)
        val list = toJsonList(getValue(key)) ?: mutableListOf()
        val position = if (index < 0 || index > list.size) list.size else index
        list.add(position, toPlain(value))
        updateConfig(key, JsonUtils.jsonMapper.writeValueAsString(list))
    }

    fun removeListElement(key: String, index: Int) {
        requireJsonItem(key)
        val list = toJsonList(getValue(key)) ?: return
        if (index !in list.indices) {
            return
        }
        list.removeAt(index)
        updateConfig(key, JsonUtils.jsonMapper.writeValueAsString(list))
    }

    fun isTrue(key: Enum<*>): Boolean {
        return isTrue(key.name)
    }

    fun isTrue(key: String): Boolean {
        return getValue(key).lowercase() == "true"
    }

    fun getAllConfig(): List<SystemItemInfo> {
        val map = mutableMapOf<String, SystemItemInfo>()
        defaultConfig.forEach { (k, v) ->
            map[k] = v.copyTo(SystemItemInfo())
        }
        systemConfigSaver.findAll().forEach {
            val c = map[it.first.trimKey()]
            if (c != null) {
                c.value = it.second
                c.isModified = true
            }
        }
        return map.values
            .map { config ->
                if (config.secret && config.secretPaths.isEmpty()) {
                    config.value = MASKED_VALUE
                } else if (config.secretPaths.isNotEmpty()) {
                    config.value = maskValue(config, config.value)
                }
                config
            }
            .map { config ->
                val item = defaultConfig[config.key.trimKey()]
                if (item != null) {
                    val formatter = item.formatter
                    if (formatter != null) {
                        config.formatedValue = formatter.apply(config.value)
                    }
                    config.schema = item.jsonType?.let { JsonSchemaGenerator.generate(it) }
                }
                config
            }
            .sortedWith(
                compareBy<SystemItemInfo> { it.group.order }
                    .thenBy { it.order }
            )
    }

    fun generateConfig(key: String, value: String?): String {
        val k = key.replace("_", "-").lowercase()
        if (value.isNullOrBlank()) {
            return "jmicro.system.config.kv.${k}="
        }
        val item = defaultConfig[key.trimKey()]
        val v = if (item != null && item.type == SystemItemType.JSON.name) {
            normalizeValue(item, value)
        } else {
            item?.valueFormatter?.apply(value) ?: value
        }
        return "jmicro.system.config.kv.${k}=${v}"
    }

    fun updateConfig(key: String, value: String) {
        val oldV = getValue(key)
        val item = defaultConfig[key.trimKey()] ?: throw MicroConfigException("配置项不存在")
        var newV = normalizeValue(item, value)
        if (item.secretPaths.isNotEmpty()) {
            newV = restoreMaskedValue(item, oldV, newV)
        }
        systemConfigSaver.save(key, newV)
        cache.put(key, newV)
        applicationEventPublisher.publishEvent(SystemConfigUpdateEvent(key, oldV, newV))
    }

    fun deleteConfig(key: String) {
        val oldV = getValue(key)
        systemConfigSaver.delete(key)
        val newV = defaultConfig[key.trimKey()]?.value ?: ""
        cache.put(key, newV)
        applicationEventPublisher.publishEvent(SystemConfigUpdateEvent(key, oldV, newV))
    }

    fun refreshConfig() {
        cache.invalidateAll()
        applicationEventPublisher.publishEvent(SystemConfigRefreshEvent())
    }

    fun getConfigByTag(tag: String): List<SystemItem> {
        val t = tag.trim().lowercase()
        return getAllConfig()
            .filter { it.tag.any { item -> item.trim().lowercase() == t } }
            .map { SystemItem(it.key, it.name, it.value) }
    }

    // ---- 内部实现 ----

    private fun requireJsonItem(key: String): SystemItemInfo {
        val item = defaultConfig[key.trimKey()] ?: throw MicroConfigException("配置项不存在")
        if (item.type != SystemItemType.JSON.name) {
            throw MicroConfigException("配置项不是 JSON 类型")
        }
        return item
    }

    /**
     * 校验并规范化配置值：JSON 项统一序列化为紧凑单行文本，保证可安全写入 properties。
     */
    private fun normalizeValue(item: SystemItemInfo, value: String): String {
        if (item.type != SystemItemType.JSON.name) {
            val formatter = item.valueFormatter ?: return value
            return try {
                formatter.apply(value)
            } catch (e: Exception) {
                throw MicroConfigException("配置项值不合法: ${e.message}")
            }
        }
        if (value.isBlank()) {
            return ""
        }
        val node = try {
            JsonUtils.jsonMapper.readTree(value)
        } catch (e: Exception) {
            throw MicroConfigException("配置项值不合法: ${e.message}")
        }
        item.jsonType?.let { type ->
            try {
                JsonUtils.jsonMapper.readValue<Any>(value, JsonUtils.jsonMapper.typeFactory.constructType(type))
            } catch (e: Exception) {
                throw MicroConfigException("配置项值不合法: ${e.message}")
            }
        }
        return JsonUtils.jsonMapper.writeValueAsString(node)
    }

    private fun maskValue(item: SystemItemInfo, value: String): String {
        val root = toJsonMap(value) ?: return value
        item.secretPaths.forEach { maskAtPath(root, splitPath(it)) }
        return JsonUtils.jsonMapper.writeValueAsString(root)
    }

    private fun restoreMaskedValue(item: SystemItemInfo, oldValue: String, newValue: String): String {
        val oldRoot = toJsonMap(oldValue) ?: return newValue
        val newRoot = toJsonMap(newValue) ?: return newValue
        item.secretPaths.forEach { restoreAtPath(oldRoot, newRoot, splitPath(it)) }
        return JsonUtils.jsonMapper.writeValueAsString(newRoot)
    }

    private fun splitPath(path: String): List<String> {
        val p = path.trim()
        if (p.isEmpty()) {
            return emptyList()
        }
        return if (p.startsWith("/")) {
            p.split("/")
                .drop(1)
                .filter { it.isNotEmpty() }
                .map { it.replace("~1", "/").replace("~0", "~") }
        } else {
            p.split(".").filter { it.isNotEmpty() }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun toJsonMap(json: String): MutableMap<String, Any?>? {
        if (json.isBlank()) {
            return null
        }
        return runCatching {
            JsonUtils.jsonMapper.readValue(json, Any::class.java) as? MutableMap<String, Any?>
        }.getOrNull()
    }

    @Suppress("UNCHECKED_CAST")
    private fun toJsonList(json: String): MutableList<Any?>? {
        if (json.isBlank()) {
            return null
        }
        return runCatching {
            JsonUtils.jsonMapper.readValue(json, Any::class.java) as? MutableList<Any?>
        }.getOrNull()
    }

    private fun toPlain(value: Any?): Any? = when (value) {
        null, is String, is Number, is Boolean -> value
        else -> JsonUtils.jsonMapper.convertValue(value, Any::class.java)
    }

    private fun getAtPath(root: Any?, segments: List<String>): Any? {
        var current = root
        for (segment in segments) {
            current = when (current) {
                is Map<*, *> -> current[segment]
                is List<*> -> segment.toIntOrNull()?.let { current.getOrNull(it) }
                else -> return null
            }
        }
        return current
    }

    @Suppress("UNCHECKED_CAST")
    private fun setAtPath(root: MutableMap<String, Any?>, segments: List<String>, value: Any?) {
        var current = root
        for (i in 0 until segments.size - 1) {
            val segment = segments[i]
            val next = current[segment]
            current = if (next is MutableMap<*, *>) {
                next as MutableMap<String, Any?>
            } else {
                LinkedHashMap<String, Any?>().also { current[segment] = it }
            }
        }
        current[segments.last()] = value
    }

    @Suppress("UNCHECKED_CAST")
    private fun maskAtPath(root: MutableMap<String, Any?>, segments: List<String>) {
        if (segments.isEmpty()) {
            return
        }
        when (val parent = getAtPath(root, segments.dropLast(1))) {
            is MutableMap<*, *> -> {
                val map = parent as MutableMap<String, Any?>
                val field = segments.last()
                if (map.containsKey(field)) {
                    map[field] = MASKED_VALUE
                }
            }

            is MutableList<*> -> {
                val list = parent as MutableList<Any?>
                val index = segments.last().toIntOrNull() ?: return
                if (index in list.indices) {
                    list[index] = MASKED_VALUE
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun restoreAtPath(
        oldRoot: MutableMap<String, Any?>,
        newRoot: MutableMap<String, Any?>,
        segments: List<String>,
    ) {
        if (segments.isEmpty()) {
            return
        }
        val newParent = getAtPath(newRoot, segments.dropLast(1)) as? MutableMap<String, Any?> ?: return
        val field = segments.last()
        if (newParent[field] != MASKED_VALUE) {
            return
        }
        val oldValue = getAtPath(oldRoot, segments) ?: return
        newParent[field] = oldValue
    }
}
