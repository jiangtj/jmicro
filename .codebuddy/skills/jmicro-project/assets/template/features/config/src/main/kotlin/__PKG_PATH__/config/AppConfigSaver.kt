// #if db
package __PKG__.config

import com.jiangtj.micro.business.config.JMicroConfigSaver
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.Table
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * 基于 `sys_config` 表的系统配置持久化（与 jmicro demo 的 DemoConfigSaver 等价）。
 *
 * 只保存被覆盖的配置项，未写入的项由 `JMicroConfigLoader` 的默认值兜底；
 * 注册为 Bean 后自动替换 `InMemoryJMicroConfigSaver`（`@ConditionalOnMissingBean`）。
 *
 * 这里用 `DSL.table/field` 手写表引用，保证生成 jOOQ 代码之前主代码也能编译；
 * 跑过 `db-generate` 之后可以换成生成的 `SysConfig.SYS_CONFIG`（demo 写法）。
 */
@Repository
class AppConfigSaver(
    private val dsl: DSLContext,
) : JMicroConfigSaver {

    private companion object {
        val TABLE: Table<Record> = DSL.table("sys_config")
        val CONFIG_KEY: Field<String> = DSL.field("config_key", String::class.java)
        val CONFIG_VALUE: Field<String> = DSL.field("config_value", String::class.java)
        val CREATE_TIME: Field<LocalDateTime> = DSL.field("create_time", LocalDateTime::class.java)
        val UPDATE_TIME: Field<LocalDateTime> = DSL.field("update_time", LocalDateTime::class.java)
    }

    override fun fetchOneByConfigKey(key: String): String? =
        dsl.select(CONFIG_VALUE)
            .from(TABLE)
            .where(CONFIG_KEY.eq(key))
            .fetchOne(CONFIG_VALUE)

    override fun findAll(): List<Pair<String, String>> =
        dsl.select(CONFIG_KEY, CONFIG_VALUE)
            .from(TABLE)
            .fetch()
            .mapNotNull { record ->
                val key = record.value1()
                val value = record.value2()
                if (key == null || value == null) null else key to value
            }

    /**
     * 先更新、未命中再插入：不依赖具体方言的 upsert 语法。
     * （手写表引用时 H2 无法为 `ON DUPLICATE KEY UPDATE` 推断主键；
     * 换成生成的 `SysConfig.SYS_CONFIG` 后可改用 `onDuplicateKeyUpdate()`，即 demo 写法。）
     */
    override fun save(key: String, value: String) {
        val now = LocalDateTime.now()
        val updated = dsl.update(TABLE)
            .set(CONFIG_VALUE, value)
            .set(UPDATE_TIME, now)
            .where(CONFIG_KEY.eq(key))
            .execute()
        if (updated == 0) {
            dsl.insertInto(TABLE)
                .set(CONFIG_KEY, key)
                .set(CONFIG_VALUE, value)
                .set(CREATE_TIME, now)
                .set(UPDATE_TIME, now)
                .execute()
        }
    }

    override fun delete(key: String) {
        dsl.deleteFrom(TABLE)
            .where(CONFIG_KEY.eq(key))
            .execute()
    }
}
// #endif
