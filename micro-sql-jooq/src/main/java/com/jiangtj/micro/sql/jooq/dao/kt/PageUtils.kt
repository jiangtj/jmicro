package com.jiangtj.micro.sql.jooq.dao.kt

import com.jiangtj.micro.sql.jooq.PageUtils
import com.jiangtj.micro.sql.jooq.QueryUtils
import com.jiangtj.micro.sql.jooq.ResultStepHandler
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.function.Function

data class PageContext<R : Record>(
    var create: DSLContext,
    var table: Table<R>,
    var conditions: MutableList<Condition?> = mutableListOf(),
    var pageable: Pageable? = null,
    var orderField: MutableList<OrderField<*>> = mutableListOf(),
)

/**
 * 创建分页查询
 */
fun <R : Record> DSLContext.selectPage(table: Table<R>): WhereStep<R> {
    return WhereStep(PageContext(this, table))
}

/**
 * Where 步骤
 */
class WhereStep<R : Record>( ctx: PageContext<R>):PageableStep<R>(ctx) {
    fun where(vararg conditions: Condition?): ConditionStep<R> {
        ctx.conditions = conditions.toMutableList()
        return ConditionStep(ctx)
    }

    fun conditionByExample(example: Any?, vararg ignoredFields: Field<*>): ConditionStep<R> {
        val exampleConditions = QueryUtils.nec(ctx.create, ctx.table, example, *ignoredFields)
        ctx.conditions += exampleConditions
        return ConditionStep(ctx)
    }

    fun conditions(vararg conditions: Condition?): ConditionStep<R> {
        return where(*conditions)
    }
}

/**
 * 条件步骤记录
 */
class ConditionStep<R : Record>(ctx: PageContext<R>):PageableStep<R>(ctx) {
    fun and(conditions: Condition?): ConditionStep<R> {
        ctx.conditions += conditions
        return this
    }
}

/**
 * 分页步骤
 */
open class PageableStep<R : Record>(val ctx: PageContext<R>) {

    fun pageable(pageable: Pageable, vararg orderFields: OrderField<*>): ResultStep<R> {
        ctx.pageable = pageable
        ctx.orderField = orderFields.toMutableList()
        return ResultStep(ctx)
    }
}

/**
 * 结果步骤
 */
class ResultStep<R : Record>(val ctx: PageContext<R>) {
    /**
     * 执行查询并获取结果。
     */
    fun fetch(): Pair<Result<R>, Int> {
        val conditions = ctx.conditions.filterNotNull().reduce { acc, condition -> acc.and(condition) }
        val listC = ctx.create.selectFrom(ctx.table).where(conditions)
        val pageC = PageUtils.handlePageable(listC, ctx.pageable!!, *ctx.orderField.toTypedArray())
        val result = pageC.fetch()
        val count = ctx.create.selectCount().where(conditions).fetchSingle().value1()
        return Pair(result, count)
    }

    /**
     * 执行查询并将结果转换为指定类型的列表。
     */
    inline fun <reified T> fetchInto(): Pair<List<T>, Int> {
        val result = fetch()
        return Pair(result.first.into(T::class.java), result.second)
    }

    /**
     * 执行查询并将结果转换为分页对象。
     */
    inline fun <reified T> fetchPage(): Page<T> {
        val result = fetchInto<T>()
        return PageImpl(result.first, ctx.pageable!!, result.second.toLong())
    }
}
