package com.jiangtj.micro.business.config

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.jiangtj.micro.web.copyTo
import org.springframework.context.ApplicationEventPublisher

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
            .map {
                if (it.secret) {
                    it.value = "******"
                }
                it
            }
            .map {
                val formatter = defaultConfig[it.key.trimKey()]?.formatter
                if (formatter != null) {
                    it.formatedValue = formatter.apply(it.value)
                }
                it
            }
            .sortedWith(
                compareBy<SystemItemInfo> { it.group.order }
                    .thenBy { it.order }
            )
    }

    fun generateConfig(key: String, value: String?): String {
        val k = key.replace("_", "-").lowercase()
        if (value.isNullOrBlank()) {
            return "system.config.kv.${k}="
        }
        val v = defaultConfig[key.trimKey()]?.valueFormatter?.apply(value) ?: value
        return "system.config.kv.${k}=${v}"
    }

    fun updateConfig(key: String, value: String) {
        val oldV = getValue(key)
        var newV = value
        val item = defaultConfig[key.trimKey()] ?: throw MicroConfigException("配置项不存在")
        val valueFormatter = item.valueFormatter
        if (valueFormatter != null) {
            try {
                newV = valueFormatter.apply(value)
            } catch (e: Exception) {
                throw MicroConfigException("配置项值不合法: ${e.message}")
            }
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

}
