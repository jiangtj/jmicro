package com.jiangtj.micro.sql.jooq.dao.kt

import com.jiangtj.micro.sql.jooq.QueryUtils
import org.jooq.*

/**
 * KtDao 类提供了一系列基于 JOOQ 的数据库操作方法，用于对指定表进行增删改查操作。
 *
 * @param R 可更新记录的类型，继承自 [UpdatableRecord]。
 * @param T 主键字段的数据类型。
 */
class KtDao<R : UpdatableRecord<R>, T> {

    // 主键字段
    val idField: TableField<R, T>
    // 操作的表
    val table: Table<R>

    /**
     * 构造函数，初始化主键字段和操作的表。
     *
     * @param idField 主键字段。
     */
    constructor(idField: TableField<R, T>) {
        this.idField = idField
        this.table = idField.table!!
    }

    /**
     * 分页查询表中的记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 查询。
     * @return 分页查询结果，具体返回类型由 `selectPage` 方法决定。
     */
    fun fetchPage(create: DSLContext) = create.selectPage(table)

    /**
     * 根据指定字段和值查询表中的记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 查询。
     * @param field 用于查询的字段。
     * @param value 用于查询的一个或多个值。
     * @return 查询到的记录结果集。
     */
    fun <V> fetch(create: DSLContext, field: TableField<R, V>, vararg value: V): Result<R> {
        return create.selectFrom(table)
            .where(conditionVararg(field, *value))
            .fetch()
    }

    /**
     * 根据主键值批量查询表中的记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 查询。
     * @param value 用于查询的一个或多个主键值。
     * @return 查询到的记录结果集。
     */
    fun fetchByIds(create: DSLContext, vararg value: T) = fetch(create, idField, *value)

    /**
     * 根据主键值查询单条记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 查询。
     * @param value 用于查询的主键值。
     * @return 查询到的单条记录，如果未找到则返回 null。
     */
    fun fetchById(create: DSLContext, value: T): R? {
        return create.selectFrom(table)
            .where(idField.eq(value))
            .fetchOne()
    }

    /**
     * 向表中插入一条记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 插入操作。
     * @param example 包含插入数据的对象。
     * @return 插入后的记录对象。
     */
    fun insert(create: DSLContext, example: Any): R {
        val record: R = create.newRecord(table, example)
        record.insert()
        return record
    }

    /**
     * 根据示例对象更新表中的记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 更新操作。
     * @param example 包含更新数据的对象。
     * @return 更新后的记录对象。
     */
    fun update(create: DSLContext, example: Any): R {
        val record: R = create.newRecord(table, example)
        QueryUtils.replaceEmptyWithNullForUpdate(record)
        val id = record.getValue(idField)
        val condition: Condition = idField.eq(id)
        create.executeUpdate(record, condition)
        return record
    }

    /**
     * 根据主键值批量删除表中的记录。
     *
     * @param create JOOQ 的 DSL 上下文，用于执行 SQL 删除操作。
     * @param value 用于确定要删除记录的一个或多个主键值。
     */
    fun deleteById(create: DSLContext, vararg value: T) {
        create.deleteFrom(table)
            .where(conditionVararg(idField, *value))
            .execute()
    }
}