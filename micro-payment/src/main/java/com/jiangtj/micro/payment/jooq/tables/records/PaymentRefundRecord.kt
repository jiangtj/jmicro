/*
 * This file is generated by jOOQ.
 */
package com.jiangtj.micro.payment.jooq.tables.records


import com.jiangtj.micro.payment.jooq.tables.PaymentRefund
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl
import java.math.BigDecimal
import java.time.LocalDateTime


/**
 * 支付退款记录
 */
@Suppress("UNCHECKED_CAST")
open class PaymentRefundRecord() : UpdatableRecordImpl<PaymentRefundRecord>(PaymentRefund.PAYMENT_REFUND) {

    open var id: Int?
        set(value): Unit = set(0, value)
        get(): Int? = get(0) as Int?

    open var createTime: LocalDateTime?
        set(value): Unit = set(1, value)
        get(): LocalDateTime? = get(1) as LocalDateTime?

    open var modifyTime: LocalDateTime?
        set(value): Unit = set(2, value)
        get(): LocalDateTime? = get(2) as LocalDateTime?

    @get:NotNull
    @get:Size(max = 50)
    open var orderNo: String?
        set(value): Unit = set(3, value)
        get(): String? = get(3) as String?

    @get:NotNull
    @get:Size(max = 50)
    open var refundNo: String?
        set(value): Unit = set(4, value)
        get(): String? = get(4) as String?

    @get:NotNull
    open var paymentId: Int?
        set(value): Unit = set(5, value)
        get(): Int? = get(5) as Int?

    @get:NotNull
    @get:Size(max = 50)
    open var paymentNo: String?
        set(value): Unit = set(6, value)
        get(): String? = get(6) as String?

    @get:Size(max = 50)
    open var channelRefundId: String?
        set(value): Unit = set(7, value)
        get(): String? = get(7) as String?

    @get:NotNull
    open var amount: BigDecimal?
        set(value): Unit = set(8, value)
        get(): BigDecimal? = get(8) as BigDecimal?

    @get:NotNull
    open var status: Byte?
        set(value): Unit = set(9, value)
        get(): Byte? = get(9) as Byte?

    @get:Size(max = 255)
    open var refundReason: String?
        set(value): Unit = set(10, value)
        get(): String? = get(10) as String?

    open var refundTime: LocalDateTime?
        set(value): Unit = set(11, value)
        get(): LocalDateTime? = get(11) as LocalDateTime?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Int?> = super.key() as Record1<Int?>

    /**
     * Create a detached, initialised PaymentRefundRecord
     */
    constructor(id: Int? = null, createTime: LocalDateTime? = null, modifyTime: LocalDateTime? = null, orderNo: String? = null, refundNo: String? = null, paymentId: Int? = null, paymentNo: String? = null, channelRefundId: String? = null, amount: BigDecimal? = null, status: Byte? = null, refundReason: String? = null, refundTime: LocalDateTime? = null): this() {
        this.id = id
        this.createTime = createTime
        this.modifyTime = modifyTime
        this.orderNo = orderNo
        this.refundNo = refundNo
        this.paymentId = paymentId
        this.paymentNo = paymentNo
        this.channelRefundId = channelRefundId
        this.amount = amount
        this.status = status
        this.refundReason = refundReason
        this.refundTime = refundTime
        resetChangedOnNotNull()
    }

    /**
     * Create a detached, initialised PaymentRefundRecord
     */
    constructor(value: com.jiangtj.micro.payment.jooq.tables.pojos.PaymentRefund?): this() {
        if (value != null) {
            this.id = value.id
            this.createTime = value.createTime
            this.modifyTime = value.modifyTime
            this.orderNo = value.orderNo
            this.refundNo = value.refundNo
            this.paymentId = value.paymentId
            this.paymentNo = value.paymentNo
            this.channelRefundId = value.channelRefundId
            this.amount = value.amount
            this.status = value.status
            this.refundReason = value.refundReason
            this.refundTime = value.refundTime
            resetChangedOnNotNull()
        }
    }
}
