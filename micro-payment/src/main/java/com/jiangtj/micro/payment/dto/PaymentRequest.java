package com.jiangtj.micro.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private String orderNo;
    private BigDecimal amount;
    private String description;
    private String notifyUrl;
}