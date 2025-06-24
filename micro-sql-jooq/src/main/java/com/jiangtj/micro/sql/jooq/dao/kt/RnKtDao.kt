package com.jiangtj.micro.sql.jooq.dao.kt

import org.jooq.DSLContext
import org.jooq.TableField
import org.jooq.TableRecord

class RnKtDao<R : TableRecord<R>, T>(
    val tableField: TableField<R, T>,
) : KtDao<R, T, R>(tableField, tableField.table!!) {

    override fun fetch(create: DSLContext, vararg value: T): Array<R> {
        if (value.isEmpty()) {
            throw IllegalArgumentException("value is empty")
        }
        if (value.size == 1) {
            return create.selectFrom(table)
                .where(queryField.eq(value[0]))
                .fetchArray()
        }
        return create.selectFrom(table)
            .where(queryField.`in`(*value))
            .fetchArray()
    }

    override fun delete(create: DSLContext, vararg value: T) {
        TODO("Not yet implemented")
    }

    override fun insert(create: DSLContext, value: T, vararg record: R) {
        TODO("Not yet implemented")
    }
}