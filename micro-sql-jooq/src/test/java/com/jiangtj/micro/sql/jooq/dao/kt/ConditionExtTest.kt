package com.jiangtj.micro.sql.jooq.dao.kt

import com.jiangtj.micro.common.exceptions.MicroProblemDetailException
import com.jiangtj.micro.sql.jooq.jooq.Tables.SYSTEM_OPERATE_RECORD
import com.jiangtj.micro.sql.jooq.jooq.Tables.SYSTEM_USER
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.jooq.tools.jdbc.MockDataProvider
import org.jooq.tools.jdbc.MockExecuteContext
import org.jooq.tools.jdbc.MockResult
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val log = KotlinLogging.logger {}

class ConditionExtTest {

    class SQLCheck(private val apply: (sql: String) -> Unit) : MockDataProvider {
        override fun execute(ctx: MockExecuteContext): Array<out MockResult> {
            val create = DSL.using(SQLDialect.MYSQL)
            val sql = ctx.sql()
            apply(sql)
            
            val result = create.newResult(SYSTEM_USER)
            result.add(create.newRecord(SYSTEM_USER).values(1, "name", "ps", 0))
            return arrayOf(MockResult(1, result))
        }
    }

    fun createDSL(apply: (sql: String) -> Unit = {}) = 
        DSL.using(MockConnection(SQLCheck(apply)), SQLDialect.MYSQL)

    @Test
    fun testAndNotDeleted() {
        val create = createDSL { sql ->
            log.info { "sql: $sql" }
            assertTrue(sql.contains("is_deleted = ?"), "SQL should contain is_deleted = ? condition")
        }
        
        val result = create.selectFrom(SYSTEM_USER)
            .where(SYSTEM_USER.ID.eq(1))
            .andNotDeleted()
            .fetch()
            
        log.info { "result: $result" }
    }

    @Test
    fun testConditionVarargSingleValue() {
        val condition = conditionVararg(SYSTEM_USER.ID, 1)
        
        val create = createDSL { sql ->
            log.info { "sql: $sql" }
            assertTrue(sql.contains("`system_user`.`id` = ?"), "SQL should contain single value condition")
        }
        
        val result = create.selectFrom(SYSTEM_USER)
            .where(condition)
            .fetch()
            
        log.info { "result: $result" }
    }

    @Test
    fun testConditionVarargMultipleValues() {
        val condition = conditionVararg(SYSTEM_USER.ID, 1, 2, 3)
        
        val create = createDSL { sql ->
            log.info { "sql: $sql" }
            assertTrue(sql.contains("`system_user`.`id` in (?, ?, ?)"), "SQL should contain IN condition")
        }
        
        val result = create.selectFrom(SYSTEM_USER)
            .where(condition)
            .fetch()
            
        log.info { "result: $result" }
    }

    @Test
    fun testConditionVarargEmptyValues() {
        val exception = assertThrows<MicroProblemDetailException> {
            conditionVararg(SYSTEM_USER.ID)
        }
        
        assertEquals("参数错误", exception.title)
        assertEquals("至少需要一个值", exception.detail)
    }

    @Test
    fun testBetweenLocalDateTime() {
        val field = SYSTEM_OPERATE_RECORD.CREATE_TIME
        val dates = listOf(
            LocalDate.of(2023, 1, 1).atStartOfDay(),
            LocalDate.of(2023, 12, 31).atTime(23, 59, 59)
        )
        
        val condition = field.between(dates)
        
        val create = createDSL { sql ->
            log.info { "sql: $sql" }
            assertTrue(sql.contains("between ? and ?"), "SQL should contain between condition")
        }
        
        val result = create.selectFrom(SYSTEM_USER)
            .where(condition)
            .fetch()
            
        log.info { "result: $result" }
    }

    @Test
    fun testBetweenLocalDate() {
        val field = SYSTEM_OPERATE_RECORD.CREATE_TIME
        val dates = listOf(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 12, 31)
        )
        
        val condition = field.between(dates)
        
        val create = createDSL { sql ->
            log.info { "sql: $sql" }
            assertTrue(sql.contains("between ? and ?"), "SQL should contain between condition")
        }
        
        val result = create.selectFrom(SYSTEM_USER)
            .where(condition)
            .fetch()
            
        log.info { "result: $result" }
    }

    @Test
    fun testBetweenLocalDateFromLocalDateList() {
        val field = SYSTEM_OPERATE_RECORD.CREATE_TIME
        val dates = listOf(
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 12, 31)
        )
        
        val condition = field.between(dates)
        
        val create = createDSL { sql ->
            log.info { "sql: $sql" }
            assertTrue(sql.contains("between ? and ?"), "SQL should contain between condition")
        }
        
        val result = create.selectFrom(SYSTEM_USER)
            .where(condition)
            .fetch()
            
        log.info { "result: $result" }
    }

    @Test
    fun testBetweenEmptyDates() {
        val field = SYSTEM_OPERATE_RECORD.CREATE_TIME
        val condition = field.between(emptyList<LocalDate>())
        
        val create = createDSL { sql ->
            log.info { "sql: $sql" }
            assertFalse(sql.contains("where"), "SQL should not contain where")
        }
        
        val result = create.selectFrom(SYSTEM_USER)
            .where(condition)
            .fetch()
            
        log.info { "result: $result" }
    }

    @Test
    fun testBetweenNullDates() {
        val field = SYSTEM_OPERATE_RECORD.CREATE_TIME
        val condition = field.between(null as List<LocalDate>?)
        
        val create = createDSL { sql ->
            log.info { "sql: $sql" }
            assertFalse(sql.contains("where"), "SQL should not contain where")
        }
        
        val result = create.selectFrom(SYSTEM_USER)
            .where(condition)
            .fetch()
            
        log.info { "result: $result" }
    }

    @Test
    fun testBetweenInvalidDateRange() {
        val field = SYSTEM_OPERATE_RECORD.CREATE_TIME
        val dates = listOf(
            LocalDate.of(2023, 12, 31),
            LocalDate.of(2023, 1, 1)
        )
        
        val exception = assertThrows<MicroProblemDetailException> {
            field.between(dates)
        }
        
        assertEquals("日期参数错误", exception.title)
        assertEquals("开始日期不能大于结束日期", exception.detail)
    }

    @Test
    fun testBetweenInvalidDateCount() {
        val field = SYSTEM_OPERATE_RECORD.CREATE_TIME
        val dates = listOf(LocalDate.of(2023, 1, 1))
        
        val exception = assertThrows<MicroProblemDetailException> {
            field.between(dates)
        }
        
        assertEquals("日期参数错误", exception.title)
        assertEquals("日期参数错误", exception.detail)
    }
}