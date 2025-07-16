package com.jiangtj.micro.payment

/**
 * 支付方式
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PayMethod(
    val method: Int,
    val channel: String,
)
