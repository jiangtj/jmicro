package com.jiangtj.micro.demobackend.config

import com.jiangtj.micro.business.config.JMicroConfigSaver
import com.jiangtj.micro.demobackend.jooq.tables.SysConfig
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * 基于 `sys_config` 表的系统配置持久化实现。
 *
 * 只保存被覆盖的配置项，未写入的配置项由 `JMicroConfigLoader` 提供的默认值兜底；
 * 新增/更新使用 `ON DUPLICATE KEY UPDATE`（H2 MySQL 模式与 MySQL 8 均支持）。
 */
@Repository
class JooqJMicroConfigSaver(
    private val dsl: DSLContext,
) : JMicroConfigSaver {

    override fun fetchOneByConfigKey(key: String): String? =
        dsl.select(SysConfig.SYS_CONFIG.CONFIG_VALUE)
            .from(SysConfig.SYS_CONFIG)
            .where(SysConfig.SYS_CONFIG.CONFIG_KEY.eq(key))
            .fetchOne(SysConfig.SYS_CONFIG.CONFIG_VALUE)

    override fun findAll(): List<Pair<String, String>> =
        dsl.select(SysConfig.SYS_CONFIG.CONFIG_KEY, SysConfig.SYS_CONFIG.CONFIG_VALUE)
            .from(SysConfig.SYS_CONFIG)
            .fetch()
            .mapNotNull { record ->
                val key = record.get(SysConfig.SYS_CONFIG.CONFIG_KEY)
                val value = record.get(SysConfig.SYS_CONFIG.CONFIG_VALUE)
                if (key == null || value == null) null else key to value
            }

    override fun save(key: String, value: String) {
        val now = LocalDateTime.now()
        dsl.insertInto(SysConfig.SYS_CONFIG)
            .set(SysConfig.SYS_CONFIG.CONFIG_KEY, key)
            .set(SysConfig.SYS_CONFIG.CONFIG_VALUE, value)
            .set(SysConfig.SYS_CONFIG.CREATE_TIME, now)
            .set(SysConfig.SYS_CONFIG.UPDATE_TIME, now)
            .onDuplicateKeyUpdate()
            .set(SysConfig.SYS_CONFIG.CONFIG_VALUE, value)
            .set(SysConfig.SYS_CONFIG.UPDATE_TIME, now)
            .execute()
    }

    override fun delete(key: String) {
        dsl.deleteFrom(SysConfig.SYS_CONFIG)
            .where(SysConfig.SYS_CONFIG.CONFIG_KEY.eq(key))
            .execute()
    }
}
