package com.jiangtj.micro.payment.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    BALANCE("balance", "余额支付"),
    WECHAT("wechat", "微信支付"),
    ALIPAY("alipay", "支付宝支付");

    private final String code;
    private final String description;

    PaymentMethod(String code, String description) {
        this.code = code;
        this.description = description;
    }
}