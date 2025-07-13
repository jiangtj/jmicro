package com.jiangtj.micro.sql.jooq.dao.kt

import com.jiangtj.micro.sql.jooq.PageUtils
import com.jiangtj.micro.sql.jooq.QueryUtils
import org.jooq.*

class KtDao<R : UpdatableRecord<R>, T> {

    val idField: TableField<R, T>
    val table: Table<R>

    constructor(idField: TableField<R, T>) {
        this.idField = idField
        this.table = idField.table!!
    }

    fun fetchPage(create: DSLContext) = PageUtils.selectFrom(create, table)!!

    fun <V> fetch(create: DSLContext, field: TableField<R, V>, vararg value: V): Result<R> {
        return create.selectFrom(table)
            .where(conditionVararg(field, *value))
            .fetch()
    }

    fun fetchByIds(create: DSLContext, vararg value: T) = fetch(create, idField, *value)

    fun fetchById(create: DSLContext, value: T): R? {
        return create.selectFrom(table)
            .where(idField.eq(value))
            .fetchOne()
    }

    fun insert(create: DSLContext, example: Any): R {
        val record: R = create.newRecord(table, example)
        record.insert()
        return record
    }

    fun update(create: DSLContext, example: Any): R {
        val record: R = create.newRecord(table, example)
        QueryUtils.replaceEmptyWithNullForUpdate(record)
        val id = record.getValue(idField)
        val condition: Condition = idField.eq(id)
        create.executeUpdate(record, condition)
        return record
    }

    fun deleteById(create: DSLContext, vararg value: T) {
        create.deleteFrom(table)
            .where(conditionVararg(idField, *value))
            .execute()
    }
}