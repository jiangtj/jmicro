/*
 * This file is generated by jOOQ.
 */
package com.jiangtj.micro.payment.jooq.tables


import com.jiangtj.micro.payment.jooq.indexes.PAYMENT_MAIN_IDX_ORDER_NO
import com.jiangtj.micro.payment.jooq.indexes.PAYMENT_MAIN_IDX_USER_ID
import com.jiangtj.micro.payment.jooq.keys.KEY_PAYMENT_MAIN_PRIMARY
import com.jiangtj.micro.payment.jooq.tables.records.PaymentMainRecord
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl
import java.math.BigDecimal
import java.time.LocalDateTime


/**
 * 支付记录
 */
@Suppress("UNCHECKED_CAST")
open class PaymentMain(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, PaymentMainRecord>?,
    parentPath: InverseForeignKey<out Record, PaymentMainRecord>?,
    aliased: Table<PaymentMainRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<PaymentMainRecord>(
    alias,
    null,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment("支付记录"),
    TableOptions.table(),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>payment_main</code>
         */
        val PAYMENT_MAIN: PaymentMain = PaymentMain()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<PaymentMainRecord> = PaymentMainRecord::class.java

    /**
     * The column <code>payment_main.id</code>. 记录ID
     */
    val ID: TableField<PaymentMainRecord, Int?> = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "记录ID")

    /**
     * The column <code>payment_main.create_time</code>. 创建时间
     */
    val CREATE_TIME: TableField<PaymentMainRecord, LocalDateTime?> = createField(DSL.name("create_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "创建时间")

    /**
     * The column <code>payment_main.modify_time</code>. 更新时间
     */
    val MODIFY_TIME: TableField<PaymentMainRecord, LocalDateTime?> = createField(DSL.name("modify_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "更新时间")

    /**
     * The column <code>payment_main.order_no</code>. 订单编号
     */
    val ORDER_NO: TableField<PaymentMainRecord, String?> = createField(DSL.name("order_no"), SQLDataType.VARCHAR(50).nullable(false), this, "订单编号")

    /**
     * The column <code>payment_main.payment_no</code>. 支付编号
     */
    val PAYMENT_NO: TableField<PaymentMainRecord, String?> = createField(DSL.name("payment_no"), SQLDataType.VARCHAR(50).nullable(false), this, "支付编号")

    /**
     * The column <code>payment_main.transaction_id</code>. 第三方交易ID
     */
    val TRANSACTION_ID: TableField<PaymentMainRecord, String?> = createField(DSL.name("transaction_id"), SQLDataType.VARCHAR(50), this, "第三方交易ID")

    /**
     * The column <code>payment_main.user_id</code>. 支付用户ID
     */
    val USER_ID: TableField<PaymentMainRecord, String?> = createField(DSL.name("user_id"), SQLDataType.VARCHAR(50).nullable(false), this, "支付用户ID")

    /**
     * The column <code>payment_main.amount</code>. 支付金额
     */
    val AMOUNT: TableField<PaymentMainRecord, BigDecimal?> = createField(DSL.name("amount"), SQLDataType.DECIMAL(12, 2).nullable(false), this, "支付金额")

    /**
     * The column <code>payment_main.status</code>. 支付状态 1 待支付 2 已支付 3 已取消 4 已退款
     * 5 已失败
     */
    val STATUS: TableField<PaymentMainRecord, Byte?> = createField(DSL.name("status"), SQLDataType.TINYINT.nullable(false), this, "支付状态 1 待支付 2 已支付 3 已取消 4 已退款 5 已失败")

    /**
     * The column <code>payment_main.payment_time</code>. 支付时间
     */
    val PAYMENT_TIME: TableField<PaymentMainRecord, LocalDateTime?> = createField(DSL.name("payment_time"), SQLDataType.LOCALDATETIME(0), this, "支付时间")

    /**
     * The column <code>payment_main.payment_method</code>. 支付平台 1 余额 2 微信 3 支付宝
     */
    val PAYMENT_METHOD: TableField<PaymentMainRecord, Byte?> = createField(DSL.name("payment_method"), SQLDataType.TINYINT.nullable(false), this, "支付平台 1 余额 2 微信 3 支付宝")

    /**
     * The column <code>payment_main.payment_channel</code>. 支付渠道 支付宝当面付
     * 微信JSAPI等
     */
    val PAYMENT_CHANNEL: TableField<PaymentMainRecord, String?> = createField(DSL.name("payment_channel"), SQLDataType.VARCHAR(50).nullable(false), this, "支付渠道 支付宝当面付 微信JSAPI等")

    private constructor(alias: Name, aliased: Table<PaymentMainRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<PaymentMainRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<PaymentMainRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>payment_main</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>payment_main</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>payment_main</code> table reference
     */
    constructor(): this(DSL.name("payment_main"), null)
    override fun getIndexes(): List<Index> = listOf(PAYMENT_MAIN_IDX_ORDER_NO, PAYMENT_MAIN_IDX_USER_ID)
    override fun getIdentity(): Identity<PaymentMainRecord, Int?> = super.getIdentity() as Identity<PaymentMainRecord, Int?>
    override fun getPrimaryKey(): UniqueKey<PaymentMainRecord> = KEY_PAYMENT_MAIN_PRIMARY
    override fun `as`(alias: String): PaymentMain = PaymentMain(DSL.name(alias), this)
    override fun `as`(alias: Name): PaymentMain = PaymentMain(alias, this)
    override fun `as`(alias: Table<*>): PaymentMain = PaymentMain(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): PaymentMain = PaymentMain(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): PaymentMain = PaymentMain(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): PaymentMain = PaymentMain(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): PaymentMain = PaymentMain(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): PaymentMain = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): PaymentMain = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): PaymentMain = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): PaymentMain = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): PaymentMain = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): PaymentMain = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): PaymentMain = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): PaymentMain = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): PaymentMain = where(DSL.notExists(select))
}
