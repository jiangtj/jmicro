package com.jiangtj.micro.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private String orderId;
    private BigDecimal amount;
    private String description;
    private String clientIp;
    private String returnUrl;
    private String notifyUrl;
}