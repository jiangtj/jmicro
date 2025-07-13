package com.jiangtj.micro.sql.jooq.dao.kt

import org.jooq.DSLContext
import org.jooq.Result
import org.jooq.TableField
import org.jooq.TableRecord

class RnKtDao<R : TableRecord<R>, T>(
    val tableField: TableField<R, T>,
) : AbstractKtDao<R, T, R>(tableField, tableField.table!!) {

    override fun fetch(create: DSLContext, vararg value: T): Result<R> {
        return create.selectFrom(table)
            .where(conditionVararg(tableField, *value))
            .fetch()
    }

    override fun delete(create: DSLContext, vararg value: T) {
        create.deleteFrom(table)
            .where(conditionVararg(tableField, *value))
            .execute()
    }

    override fun insert(create: DSLContext, value: T, vararg record: R) {
        val r = record.map {
            it[tableField] = value
            return@map it
        }
        create.batchInsert(r).execute()
    }
}