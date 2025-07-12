package com.jiangtj.micro.payment.entity;

import com.jiangtj.micro.payment.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRecord {
    private Long id;
    private String paymentId;
    private String orderId;
    private Long userId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime paymentTime;
    private String transactionId;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}