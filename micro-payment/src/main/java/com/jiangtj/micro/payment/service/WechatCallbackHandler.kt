package com.jiangtj.micro.payment.service

import com.wechat.pay.java.core.exception.ValidationException
import com.wechat.pay.java.core.notification.NotificationConfig
import com.wechat.pay.java.core.notification.NotificationParser
import com.wechat.pay.java.core.notification.RequestParam
import com.wechat.pay.java.service.payments.model.Transaction
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

private val log = KotlinLogging.logger {}

class WechatCallbackHandler(private val config: NotificationConfig) {

    fun handle(headers: HttpHeaders, body: String): ResponseEntity<String> {
        /**
         * HTTP 请求体 body。切记使用原始报文，不要用 JSON 对象序列化后的字符串，避免验签的 body 和原文不一致。
         * HTTP 头 Wechatpay-Signature。应答的微信支付签名。
         * HTTP 头 Wechatpay-Serial。微信支付平台证书的序列号，验签必须使用序列号对应的微信支付平台证书。
         * HTTP 头 Wechatpay-Nonce。签名中的随机数。
         * HTTP 头 Wechatpay-Timestamp。签名中的时间戳。
         * HTTP 头 Wechatpay-Signature-Type。签名类型。
         */
        val wechatSignature = headers.getFirst("Wechatpay-Signature")
        val wechatPaySerial = headers.getFirst("Wechatpay-Serial")
        val wechatpayNonce = headers.getFirst("Wechatpay-Nonce")
        val wechatTimestamp = headers.getFirst("Wechatpay-Timestamp")
        val wechatSignatureType = headers.getFirst("Wechatpay-Signature-Type")

        val requestParam = RequestParam.Builder()
            .serialNumber(wechatPaySerial)
            .nonce(wechatpayNonce)
            .signature(wechatSignature)
            .timestamp(wechatTimestamp)
            .body(body)
            .build()

        // 初始化 NotificationParser
        val parser = NotificationParser(config);

        try {
            // 以支付通知回调为例，验签、解密并转换成 Transaction
            val transaction = parser.parse(requestParam, Transaction::class.java)
        } catch (e: ValidationException) {
            // 签名验证失败，返回 401 UNAUTHORIZED 状态码
            log.error(e) { "sign verification failed" }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        // 处理成功，返回 200 OK 状态码
        return ResponseEntity.status(HttpStatus.OK).build()
    }

}