package com.jiangtj.micro.business.config

import java.util.concurrent.ConcurrentHashMap

class InMemorySystemConfigSaver : SystemConfigSaver {

    private val store: MutableMap<String, String> = ConcurrentHashMap()

    override fun fetchOneByConfigKey(key: String): String? {
        return store[key]
    }

    override fun findAll(): List<Pair<String, String>> {
        return store.map { it.key to it.value }
    }

    override fun save(key: String, value: String) {
        store[key] = value
    }

    override fun delete(key: String) {
        store.remove(key)
    }
}
