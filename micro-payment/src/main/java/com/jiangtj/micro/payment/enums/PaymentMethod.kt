package com.jiangtj.micro.payment.enums

enum class PaymentMethod(val code: String, val description: String) {
    BALANCE("balance", "余额支付"),
    WECHAT("wechat", "微信支付"),
    ALIPAY("alipay", "支付宝支付");
}
