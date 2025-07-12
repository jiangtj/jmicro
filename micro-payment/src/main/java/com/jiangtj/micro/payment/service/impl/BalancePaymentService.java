package com.jiangtj.micro.payment.service.impl;

import com.jiangtj.micro.common.exceptions.MicroHttpException;
import com.jiangtj.micro.payment.dto.PaymentRequest;
import com.jiangtj.micro.payment.dto.PaymentResponse;
import com.jiangtj.micro.payment.entity.PaymentRecord;
import com.jiangtj.micro.payment.enums.PaymentMethod;
import com.jiangtj.micro.payment.enums.PaymentStatus;
import com.jiangtj.micro.payment.service.PaymentService;
import com.jiangtj.micro.payment.service.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 余额支付服务实现类
 */
@Service
@RequiredArgsConstructor
public class BalancePaymentService implements PaymentService {

    private final UserBalanceService userBalanceService;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        String paymentId = UUID.randomUUID().toString().replace("-", "");
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(paymentId);
        response.setOrderId(request.getOrderId());
        response.setAmount(request.getAmount());

        try {
            // 1. 验证用户余额
            Long userId = 1L;// todo 获取用户
                // Long userId = SecurityUtils.getCurrentUserId();
            BigDecimal currentBalance = userBalanceService.getUserBalance(userId);
            if (currentBalance.compareTo(request.getAmount()) < 0) {
                response.setStatus(PaymentStatus.FAILURE);
                response.setErrorMessage("余额不足，当前余额: " + currentBalance);
                return response;
            }

            // 2. 扣减用户余额
            userBalanceService.deductBalance(userId, request.getAmount(), "订单支付: " + request.getOrderId());

            // 3. 记录支付记录
            PaymentRecord record = new PaymentRecord();
            record.setPaymentId(paymentId);
            record.setOrderId(request.getOrderId());
            record.setUserId(userId);
            record.setAmount(request.getAmount());
            record.setStatus(PaymentStatus.SUCCESS);
            record.setPaymentTime(LocalDateTime.now());
            // todo 保存支付记录
            // paymentRecordRepository.save(record);

            // 4. 返回支付结果
            response.setStatus(PaymentStatus.SUCCESS);
            response.setPaidTime(LocalDateTime.now());
            return response;
        } catch (Exception e) {
            response.setStatus(PaymentStatus.FAILURE);
            response.setErrorMessage("支付处理失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public PaymentResponse queryPayment(String paymentId) {
        throw new MicroHttpException(500, "Not implemented");
        /*return paymentRecordRepository.findByPaymentId(paymentId)
                .map(record -> {
                    PaymentResponse response = new PaymentResponse();
                    response.setPaymentId(record.getPaymentId());
                    response.setOrderId(record.getOrderId());
                    response.setAmount(record.getAmount());
                    response.setStatus(record.getStatus());
                    response.setPaidTime(record.getPaymentTime());
                    return response;
                })
                .orElseGet(() -> {
                    PaymentResponse response = new PaymentResponse();
                    response.setPaymentId(paymentId);
                    response.setStatus(PaymentStatus.FAILURE);
                    response.setErrorMessage("支付记录不存在");
                    return response;
                });*/
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(String paymentId) {
        throw new MicroHttpException(500, "Not implemented");
        /*return paymentRecordRepository.findByPaymentId(paymentId)
                .map(record -> {
                    if (record.getStatus() != PaymentStatus.SUCCESS) {
                        PaymentResponse response = new PaymentResponse();
                        response.setPaymentId(paymentId);
                        response.setStatus(record.getStatus());
                        response.setErrorMessage("只有成功的支付才能取消");
                        return response;
                    }

                    // 退款处理
                    userBalanceService.refundBalance(record.getUserId(), record.getAmount(), "订单取消退款: " + record.getOrderId());
                    record.setStatus(PaymentStatus.REFUNDED);
                    paymentRecordRepository.save(record);

                    PaymentResponse response = new PaymentResponse();
                    response.setPaymentId(paymentId);
                    response.setOrderId(record.getOrderId());
                    response.setAmount(record.getAmount());
                    response.setStatus(PaymentStatus.REFUNDED);
                    return response;
                })
                .orElseGet(() -> {
                    PaymentResponse response = new PaymentResponse();
                    response.setPaymentId(paymentId);
                    response.setStatus(PaymentStatus.FAILURE);
                    response.setErrorMessage("支付记录不存在");
                    return response;
                });*/
    }

    @Override
    public String handlePaymentNotify(String notifyData) {
        throw new MicroHttpException(500, "Not implemented");
        // 余额支付为即时到账，无需处理异步通知
        // return "success";
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.BALANCE;
    }
}