package com.jiangtj.micro.sql.jooq.dao.kt

import org.jooq.DSLContext
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableRecord

abstract class KtDao<R1 : TableRecord<R1>, T1, R2 : TableRecord<R2>>(
    val queryField: TableField<R1, T1>,
    val table: Table<R2>
) {
    abstract fun fetch(create: DSLContext, vararg value: T1): Array<R2>

    abstract fun delete(create: DSLContext, vararg value: T1)

    abstract fun insert(create: DSLContext, value: T1, vararg record: R1)
}