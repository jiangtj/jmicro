package com.jiangtj.micro.sql.jooq.dao.kt

import com.jiangtj.micro.common.exceptions.MicroProblemDetailException
import org.jooq.Condition
import org.jooq.TableField
import org.jooq.TableRecord
import org.jooq.impl.DSL

/**
 * 扩展 [Condition] 类，添加一个用于筛选未删除记录的条件。
 * 该方法会在原条件基础上添加一个新条件，要求 `is_deleted` 字段的值等于 0。
 *
 * @return 包含未删除记录筛选条件的新 [Condition] 对象。
 */
fun Condition.andNotDeleted(): Condition {
    return this.and(DSL.field("is_deleted").eq(0))
}

/**
 * 根据给定的表字段和可变参数值创建查询条件。
 * 如果没有提供任何值，会抛出 [MicroProblemDetailException] 异常。
 * 如果只提供一个值，会生成字段等于该值的条件。
 * 如果提供多个值，会生成字段值在这些值范围内的条件。
 *
 * @param R1 表记录的类型，继承自 [TableRecord]。
 * @param T1 表字段的数据类型。
 * @param queryField 用于查询的表字段。
 * @param value 用于构建查询条件的可变参数值。
 * @return 基于输入值生成的 [Condition] 对象。
 * @throws MicroProblemDetailException 当没有提供任何值时抛出该异常。
 */
fun <R1 : TableRecord<R1>, T1> conditionVararg(queryField: TableField<R1, T1>, vararg value: T1): Condition {
    if (value.isEmpty()) {
        throw MicroProblemDetailException(500, "参数错误 ", "至少需要一个值")
    }
    if (value.size == 1) {
        return queryField.eq(value[0])
    }
    return queryField.`in`(*value)
}
