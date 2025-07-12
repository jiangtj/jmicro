package com.jiangtj.micro.sql.jooq.dao.kt

import org.jooq.DSLContext
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableRecord

abstract class AbstractKtDao<R1 : TableRecord<R1>, T1, R2 : TableRecord<R2>>(
    val queryField: TableField<R1, T1>,
    val table: Table<R2>
) {
    abstract fun fetch(create: DSLContext, vararg value: T1): Array<R2>

    abstract fun delete(create: DSLContext, vararg value: T1)

    abstract fun insert(create: DSLContext, value: T1, vararg record: R1)

    fun <T2, R3 : TableRecord<R3>> createLink(
        tableField1: TableField<R2, T2>,
        tableField2: TableField<R3, T2>
    ): RNLinkKtDao<R1, T1, R2, T2, R3> {
        return RNLinkKtDao(this, tableField1, tableField2)
    }
}