package com.jiangtj.micro.payment

import com.jiangtj.micro.payment.jooq.tables.pojos.PaymentCallback
import com.jiangtj.micro.payment.jooq.tables.pojos.PaymentMain
import com.jiangtj.micro.payment.jooq.tables.pojos.PaymentRefund

data class PayCallbackContext(
    val main: PaymentMain,
    val refund: PaymentRefund,
    val notifyData: PaymentCallback,
)

interface PayCallback {
    /**
     * 支付成功
     */
    fun onSuccess(ctx: PayCallbackContext)

    /**
     * 支付失败
     */
    fun onFailed(ctx: PayCallbackContext)

    /**
     * 支付退款成功
     */
    fun onRefundSuccess(ctx: PayCallbackContext)

    /**
     * 支付退款失败
     */
    fun onRefundFailed(ctx: PayCallbackContext)
}