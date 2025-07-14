package com.jiangtj.micro.sql.jooq.dao.kt

import org.jooq.*

/**
 * 抽象的 Kotlin DAO 类，提供基本的数据访问操作方法。
 *
 * @param R1 第一个表记录的类型，继承自 [TableRecord]。
 * @param T1 查询字段的数据类型。
 * @param R2 第二个表记录的类型，继承自 [TableRecord]。
 * @param queryField 用于查询的表字段。
 * @param table 操作的目标表。
 */
abstract class AbstractKtDao<R1 : TableRecord<R1>, T1, R2 : TableRecord<R2>>(
    val queryField: TableField<R1, T1>,
    val table: Table<R2>
) {
    /**
     * 根据给定的值从数据库中获取记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 查询。
     * @param value 用于查询的一个或多个值。
     * @return 查询结果集，包含符合条件的记录。
     */
    abstract fun fetch(create: DSLContext, vararg value: T1): Result<R2>

    /**
     * 根据给定的值从数据库中删除记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 删除操作。
     * @param value 用于确定要删除记录的一个或多个值。
     */
    abstract fun delete(create: DSLContext, vararg value: T1)

    /**
     * 向数据库中插入记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 插入操作。
     * @param value 插入操作相关的值。
     * @param record 要插入的一个或多个记录。
     */
    abstract fun insert(create: DSLContext, value: T1, vararg record: R1)

    /**
     * 创建一个关联 DAO 实例，用于处理多表关联操作。
     * 此函数目前未被使用。
     *
     * @param T2 关联字段的数据类型。
     * @param R3 需查询表记录的类型，继承自 [TableRecord]。
     * @param tableField1 当前表的关联字段。
     * @param tableField2 查询表的关联字段。
     * @return 一个 [RNLinkKtDao] 实例，用于处理关联操作。
     */
    fun <T2, R3 : TableRecord<R3>> createLink(
        tableField1: TableField<R2, T2>,
        tableField2: TableField<R3, T2>
    ): RNLinkKtDao<R1, T1, R2, T2, R3> {
        return RNLinkKtDao(this, tableField1, tableField2)
    }
}