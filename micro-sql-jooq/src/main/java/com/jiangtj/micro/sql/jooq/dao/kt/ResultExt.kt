package com.jiangtj.micro.sql.jooq.dao.kt

import org.jooq.Record
import org.jooq.Result
import org.jooq.ResultQuery

/**
 * 将 [ResultQuery] 查询结果转换为指定类型的列表。
 *
 * 此函数利用 Kotlin 的内联函数和具体化类型参数特性，允许在运行时获取泛型类型 `T`。
 * 调用 JOOQ 的 `fetchInto` 方法执行查询，并将查询结果中的记录转换为指定类型 `T` 的列表。
 *
 * @param T 目标类型，由调用者在使用时指定。
 * @return 包含转换后对象的列表。
 */
inline fun <reified T> ResultQuery<out Record>.fetchInto(): List<T> = this.fetchInto(T::class.java)

/**
 * 执行 [ResultQuery] 查询并将查询结果的第一条记录转换为指定类型的对象。
 *
 * 此函数利用 Kotlin 的内联函数和具体化类型参数特性，允许在运行时获取泛型类型 `T`。
 * 调用 JOOQ 的 `fetchOneInto` 方法执行查询，若查询结果不为空，则将第一条记录转换为指定类型 `T` 的对象；
 * 若查询结果为空，则返回 `null`。
 *
 * @param T 目标类型，由调用者在使用时指定。
 * @return 转换后的指定类型对象，若查询结果为空则返回 `null`。
 */
inline fun <reified T> ResultQuery<out Record>.fetchOneInto(): T? = this.fetchOneInto(T::class.java)

/**
 * 将 [Result] 对象转换为指定类型的列表。
 *
 * 此函数利用 Kotlin 的内联函数和具体化类型参数特性，允许在运行时获取泛型类型 `T`，
 * 并调用 JOOQ 的 `into` 方法将 `Result` 对象中的所有记录转换为指定类型 `T` 的列表。
 *
 * @param T 目标类型，由调用者在使用时指定。
 * @return 包含转换后对象的列表。
 */
inline fun <reified T> Result<out Record>.into(): List<T> = this.into(T::class.java)
