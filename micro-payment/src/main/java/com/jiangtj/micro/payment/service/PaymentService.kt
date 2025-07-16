package com.jiangtj.micro.payment.service

import com.jiangtj.micro.payment.dto.PaymentRequest
import com.jiangtj.micro.payment.dto.PaymentResponse

interface PaymentService {
    /**
     * 创建支付
     * @param request 支付请求参数
     * @return 支付响应结果
     */
    fun createPayment(request: PaymentRequest): PaymentResponse

    /**
     * 查询支付状态
     * @param paymentId 支付ID
     * @return 支付响应结果
     */
    fun queryPayment(paymentId: String): PaymentResponse

    /**
     * 取消支付
     * @param paymentId 支付ID
     * @return 支付响应结果
     */
    fun cancelPayment(paymentId: String): PaymentResponse

    /**
     * 处理支付回调
     * @param notifyData 回调数据
     * @return 处理结果
     */
    fun handlePaymentNotify(notifyData: String): String

    /*
     * 处理支付回调
     * @param notifyData 回调数据
     * @return 处理结果
     */
    fun refund(notifyData: String): String

    /**
     * 处理支付回调
     * @param notifyData 回调数据
     * @return 处理结果
     */
    fun handleRefundNotify(notifyData: String): String
}