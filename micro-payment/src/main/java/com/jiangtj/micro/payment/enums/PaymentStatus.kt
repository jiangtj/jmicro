package com.jiangtj.micro.payment.enums

enum class PaymentStatus(val code: String, val description: String) {
    PENDING("pending", "待支付"),
    SUCCESS("success", "支付成功"),
    FAILURE("failure", "支付失败"),
    REFUNDED("refunded", "已退款"),
    CANCELLED("cancelled", "已取消"),
    TIMEOUT("timeout", "支付超时");
}
