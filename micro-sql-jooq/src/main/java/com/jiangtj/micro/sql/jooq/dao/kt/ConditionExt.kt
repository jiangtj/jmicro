package com.jiangtj.micro.sql.jooq.dao.kt

import com.jiangtj.micro.common.exceptions.MicroProblemDetailException
import org.jooq.Condition
import org.jooq.Field
import org.jooq.SelectConditionStep
import org.jooq.TableField
import org.jooq.TableRecord
import org.jooq.impl.DSL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDate

/**
 * 扩展 [SelectConditionStep] 类，添加一个用于筛选未删除记录的条件。
 * 该方法会在原条件基础上添加一个新条件，要求 `is_deleted` 字段的值等于 0。
 *
 * @return 包含未删除记录筛选条件的新 [SelectConditionStep] 对象。
 */
fun <R: org.jooq.Record> SelectConditionStep<R>.andNotDeleted(): SelectConditionStep<R> {
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

/**
 * 验证日期范围参数的有效性
 *
 * @param dates 日期列表
 * @throws MicroProblemDetailException 当日期参数不满足要求时抛出
 */
private fun <T : ChronoLocalDate> validateDateRange(dates: List<T>): Pair<T, T> {
    if (dates.size != 2) {
        throw MicroProblemDetailException(400, "日期参数错误", "日期参数错误")
    }
    if (dates[0] > dates[1]) {
        throw MicroProblemDetailException(400, "日期参数错误", "开始日期不能大于结束日期")
    }
    return Pair(dates[0], dates[1])
}


/**
 * 创建一个条件，用于筛选指定日期范围。
 *
 * @param dates 日期列表，包含开始日期和结束日期。
 * @return 基于给定日期列表生成的 [Condition] 对象。
 * @throws MicroProblemDetailException 当日期列表为空或长度不为 2 时抛出该异常。
 */
fun Field<LocalDateTime>.between(dates: List<LocalDate>?): Condition {
    if (dates.isNullOrEmpty()) {
        return DSL.noCondition()
    }
    val (startDate, endDate) = validateDateRange(dates)
    return this.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
}

/**
 * 创建一个条件，用于筛选指定日期范围。
 *
 * @param dates 日期列表，包含开始日期和结束日期。
 * @return 基于给定日期列表生成的 [Condition] 对象。
 * @throws MicroProblemDetailException 当日期列表为空或长度不为 2 时抛出该异常。
 */
fun Field<LocalDateTime>.between(dates: List<LocalDateTime>?): Condition {
    if (dates.isNullOrEmpty()) {
        return DSL.noCondition()
    }
    if (dates.size != 2) {
        throw MicroProblemDetailException(400, "日期参数错误", "日期参数错误")
    }
    if (dates[0] > dates[1]) {
        throw MicroProblemDetailException(400, "日期参数错误", "开始日期不能大于结束日期")
    }
    return this.between(dates[0], dates[1])
}


/**
 * 创建一个条件，用于筛选指定日期范围。
 *
 * @param dates 日期列表，包含开始日期和结束日期。
 * @return 基于给定日期列表生成的 [Condition] 对象。
 * @throws MicroProblemDetailException 当日期列表为空或长度不为 2 时抛出该异常。
 */
fun Field<LocalDate>.between(dates: List<LocalDate>?): Condition {
    if (dates.isNullOrEmpty()) {
        return DSL.noCondition()
    }
    val (startDate, endDate) = validateDateRange(dates)
    return this.between(startDate, endDate)
}
