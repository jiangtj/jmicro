package com.jiangtj.micro.payment.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("pending", "待支付"),
    SUCCESS("success", "支付成功"),
    FAILURE("failure", "支付失败"),
    REFUNDED("refunded", "已退款"),
    CANCELLED("cancelled", "已取消"),
    TIMEOUT("timeout", "支付超时");

    private final String code;
    private final String description;

    PaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}