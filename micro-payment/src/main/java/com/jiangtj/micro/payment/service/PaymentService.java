package com.jiangtj.micro.payment.service;

import com.jiangtj.micro.payment.dto.PaymentRequest;
import com.jiangtj.micro.payment.dto.PaymentResponse;
import com.jiangtj.micro.payment.enums.PaymentMethod;

public interface PaymentService {
    /**
     * 创建支付
     * @param request 支付请求参数
     * @return 支付响应结果
     */
    PaymentResponse createPayment(PaymentRequest request);

    /**
     * 查询支付状态
     * @param paymentId 支付ID
     * @return 支付响应结果
     */
    PaymentResponse queryPayment(String paymentId);

    /**
     * 取消支付
     * @param paymentId 支付ID
     * @return 支付响应结果
     */
    PaymentResponse cancelPayment(String paymentId);

    /**
     * 处理支付回调
     * @param notifyData 回调数据
     * @return 处理结果
     */
    String handlePaymentNotify(String notifyData);

    /**
     * 获取支付方式
     * @return 支付方式
     */
    PaymentMethod getPaymentMethod();
}