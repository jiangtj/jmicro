package com.jiangtj.micro.business.config

interface SystemConfigSaver {

    fun fetchOneByConfigKey(key: String): String?

    fun findAll(): List<Pair<String, String>>

    fun save(key: String, value: String)

    fun delete(key: String)
}