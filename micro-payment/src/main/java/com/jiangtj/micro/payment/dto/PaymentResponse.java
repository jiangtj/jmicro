package com.jiangtj.micro.payment.dto;

import com.jiangtj.micro.payment.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private String paymentUrl;
    private LocalDateTime paidTime;
    private String errorMessage;
}