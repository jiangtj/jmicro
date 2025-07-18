package com.jiangtj.micro.sql.jooq.dao.kt

import com.jiangtj.micro.sql.jooq.jooq.Tables.SYSTEM_USER
import com.jiangtj.micro.sql.jooq.jooq.tables.pojos.SystemUser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.jooq.tools.jdbc.MockDataProvider
import org.jooq.tools.jdbc.MockExecuteContext
import org.jooq.tools.jdbc.MockResult
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals

private val log = KotlinLogging.logger {}

fun isCount(sql: String) = sql.lowercase().startsWith("select count(*)")

class PageUtilsTest {

    class SQLCheck(private val apply: (sql: String) -> Unit) : MockDataProvider {
        override fun execute(ctx: MockExecuteContext): Array<out MockResult> {
            val create = DSL.using(SQLDialect.MYSQL)
            val sql = ctx.sql()
            apply(sql)
            if (isCount(sql)) {
                val countField = DSL.field("count(*)", Long::class.java)
                val result = create.newResult(countField)
                result.add(create.newRecord(countField).values(1))
                return arrayOf(MockResult(1, result))
            }
            val result = create.newResult(SYSTEM_USER)
            result.add(create.newRecord(SYSTEM_USER).values(1, "name", "ps", 0))
            return arrayOf(MockResult(1, result))
        }
    }

    fun createDSL(apply: (sql: String) -> Unit = {}) = DSL.using(MockConnection(SQLCheck(apply)), SQLDialect.MYSQL)


    @Test
    fun testMock() {
        val result = createDSL().selectPage(SYSTEM_USER)
            .where(SYSTEM_USER.ID.eq(1))
            .pageable(PageRequest.of(0, 10))
            .fetchPage<SystemUser>()
        log.info { "result: $result" }
    }

    @Test
    fun testNoWhere() {
        val create = createDSL {
            log.info { "sql: $it" }
            if (isCount(it)) {
                assertEquals("select count(*) from `system_user`", it)
            } else {
                assertEquals("select `system_user`.`id`, `system_user`.`username`, `system_user`.`password`, `system_user`.`is_deleted` from `system_user` limit ? offset ?", it)
            }
        }
        val result = create.selectPage(SYSTEM_USER)
            .pageable(PageRequest.of(0, 10))
            .fetchPage<SystemUser>()
        log.info { "result: $result" }
    }

    @Test
    fun testWithWhere() {
        val create = createDSL {
            log.info { "sql: $it" }
            if (isCount(it)) {
                assertEquals("select count(*) from `system_user` where `system_user`.`id` = ?", it)
            } else {
                assertEquals("select `system_user`.`id`, `system_user`.`username`, `system_user`.`password`, `system_user`.`is_deleted` from `system_user` where `system_user`.`id` = ? limit ? offset ?", it)
            }
        }
        val result = create.selectPage(SYSTEM_USER)
            .where(SYSTEM_USER.ID.eq(1))
            .pageable(PageRequest.of(0, 10))
            .fetchPage<SystemUser>()
        log.info { "result: $result" }
    }

    @Test
    fun testWithMultiWhere() {
        val create = createDSL {
            log.info { "sql: $it" }
            if (isCount(it)) {
                assertEquals("select count(*) from `system_user` " +
                        "where (`system_user`.`id` = ? and `system_user`.`username` = ?)", it)
            } else {
                assertEquals("select `system_user`.`id`, `system_user`.`username`, `system_user`.`password`, `system_user`.`is_deleted` from `system_user` " +
                        "where (`system_user`.`id` = ? and `system_user`.`username` = ?) limit ? offset ?", it)
            }
        }
        val result = create.selectPage(SYSTEM_USER)
            .where(SYSTEM_USER.ID.eq(1), null, SYSTEM_USER.USERNAME.eq("name"))
            .pageable(PageRequest.of(0, 10))
            .fetchPage<SystemUser>()
        log.info { "result: $result" }
    }

    @Test
    fun testWithWhereAnd() {
        val create = createDSL {
            log.info { "sql: $it" }
            if (isCount(it)) {
                assertEquals("select count(*) from `system_user` " +
                        "where (`system_user`.`id` = ? and `system_user`.`username` = ?)", it)
            } else {
                assertEquals("select `system_user`.`id`, `system_user`.`username`, `system_user`.`password`, `system_user`.`is_deleted` from `system_user` " +
                        "where (`system_user`.`id` = ? and `system_user`.`username` = ?) limit ? offset ?", it)
            }
        }
        val result = create.selectPage(SYSTEM_USER)
            .where(SYSTEM_USER.ID.eq(1))
            .and(SYSTEM_USER.USERNAME.eq("name"))
            .pageable(PageRequest.of(0, 10))
            .fetchPage<SystemUser>()
        log.info { "result: $result" }
    }

    @Test
    fun testWithConditionByExample() {
        val create = createDSL {
            log.info { "sql: $it" }
            if (isCount(it)) {
                assertEquals("select count(*) from `system_user` " +
                        "where (`system_user`.`id` = ? and `system_user`.`username` = ?)", it)
            } else {
                assertEquals("select `system_user`.`id`, `system_user`.`username`, `system_user`.`password`, `system_user`.`is_deleted` from `system_user` " +
                        "where (`system_user`.`id` = ? and `system_user`.`username` = ?) limit ? offset ?", it)
            }
        }
        val user = SystemUser(1, "name", null, null)
        val result = create.selectPage(SYSTEM_USER)
            .conditionByExample(user)
            .pageable(PageRequest.of(0, 10))
            .fetchPage<SystemUser>()
        log.info { "result: $result" }
    }

    @Test
    fun testWithConditionByExampleAndIgnore() {
        val create = createDSL {
            log.info { "sql: $it" }
            if (isCount(it)) {
                assertEquals("select count(*) from `system_user` where `system_user`.`id` = ?", it)
            } else {
                assertEquals("select `system_user`.`id`, `system_user`.`username`, `system_user`.`password`, `system_user`.`is_deleted` from `system_user` where `system_user`.`id` = ? limit ? offset ?", it)
            }
        }
        val user = SystemUser(1, "name", null, null)
        val result = create.selectPage(SYSTEM_USER)
            .conditionByExample(user, SYSTEM_USER.USERNAME)
            .pageable(PageRequest.of(0, 10))
            .fetchPage<SystemUser>()
        log.info { "result: $result" }
    }

}