package com.jiangtj.micro.sql.jooq.dao.kt

import org.jooq.DSLContext
import org.jooq.Result
import org.jooq.TableField
import org.jooq.TableRecord

class RNLinkKtDao<R1 : TableRecord<R1>, T1, R2 : TableRecord<R2>, T2, R3 : TableRecord<R3>>(
    val originalDao: AbstractKtDao<R1, T1, R2>,
    val tableField1: TableField<R2, T2>,
    val tableField2: TableField<R3, T2>
) : AbstractKtDao<R1, T1, R3>(originalDao.queryField, tableField2.getTable()!!) {
    override fun fetch(create: DSLContext, vararg value: T1): Result<R3> {
        val fetch = this.originalDao.fetch(create, *value)
        val list = fetch.map { it.get(tableField1) }
        return create.selectFrom(tableField2.getTable())
            .where(tableField2.`in`(list))
            .fetch()
    }

    override fun delete(create: DSLContext, vararg value: T1) {
        this.originalDao.delete(create, *value)
    }

    override fun insert(create: DSLContext, value: T1, vararg record: R1) {
        this.originalDao.insert(create, value, *record)
    }
}
