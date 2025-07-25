/*
 * This file is generated by jOOQ.
 */
package com.jiangtj.micro.payment.jooq.keys


import com.jiangtj.micro.payment.jooq.tables.PaymentCallback
import com.jiangtj.micro.payment.jooq.tables.PaymentMain
import com.jiangtj.micro.payment.jooq.tables.PaymentRefund
import com.jiangtj.micro.payment.jooq.tables.records.PaymentCallbackRecord
import com.jiangtj.micro.payment.jooq.tables.records.PaymentMainRecord
import com.jiangtj.micro.payment.jooq.tables.records.PaymentRefundRecord

import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val KEY_PAYMENT_CALLBACK_PRIMARY: UniqueKey<PaymentCallbackRecord> = Internal.createUniqueKey(PaymentCallback.PAYMENT_CALLBACK, DSL.name("KEY_payment_callback_PRIMARY"), arrayOf(PaymentCallback.PAYMENT_CALLBACK.ID), true)
val KEY_PAYMENT_MAIN_PRIMARY: UniqueKey<PaymentMainRecord> = Internal.createUniqueKey(PaymentMain.PAYMENT_MAIN, DSL.name("KEY_payment_main_PRIMARY"), arrayOf(PaymentMain.PAYMENT_MAIN.ID), true)
val KEY_PAYMENT_REFUND_PRIMARY: UniqueKey<PaymentRefundRecord> = Internal.createUniqueKey(PaymentRefund.PAYMENT_REFUND, DSL.name("KEY_payment_refund_PRIMARY"), arrayOf(PaymentRefund.PAYMENT_REFUND.ID), true)
