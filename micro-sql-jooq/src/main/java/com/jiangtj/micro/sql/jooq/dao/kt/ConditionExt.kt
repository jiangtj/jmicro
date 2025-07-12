package com.jiangtj.micro.sql.jooq.dao.kt

import com.jiangtj.micro.common.exceptions.MicroProblemDetailException
import org.jooq.Condition
import org.jooq.TableField
import org.jooq.TableRecord
import org.jooq.impl.DSL

fun Condition.andNotDeleted(): Condition {
    return this.and(DSL.field("is_deleted").eq(0))
}


fun <R1 : TableRecord<R1>, T1> conditionVararg(queryField: TableField<R1, T1>, vararg value: T1): Condition {
    if (value.isEmpty()) {
        throw MicroProblemDetailException(500, "参数错误 ", "至少需要一个值")
    }
    if (value.size == 1) {
        return queryField.eq(value[0])
    }
    return queryField.`in`(*value)
}
