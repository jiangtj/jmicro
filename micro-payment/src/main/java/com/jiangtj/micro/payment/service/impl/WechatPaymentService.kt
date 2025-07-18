package com.jiangtj.micro.payment.service.impl

import com.jiangtj.micro.common.exceptions.MicroHttpException
import com.jiangtj.micro.payment.dto.PaymentRequest
import com.jiangtj.micro.payment.dto.PaymentResponse
import com.jiangtj.micro.payment.service.PaymentService
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
class WechatPaymentService : PaymentService {
    override fun createPayment(request: PaymentRequest): PaymentResponse {
        throw MicroHttpException(500, "Not implemented")
        /*try {
            // 创建微信支付订单
            com.github.binarywang.wxpay.bean.order.WxPayUnifiedOrderRequest orderRequest = new com.github.binarywang.wxpay.bean.order.WxPayUnifiedOrderRequest();
            String outTradeNo = UUID.randomUUID().toString().replace("-", "");
            orderRequest.setOutTradeNo(outTradeNo);
            orderRequest.setTotalFee(request.getAmount().multiply(new java.math.BigDecimal("100")).intValue()); // 转换为分
            orderRequest.setBody(request.getDescription());
            orderRequest.setSpbillCreateIp(request.getClientIp());
            orderRequest.setNotifyUrl(request.getNotifyUrl());
            orderRequest.setTradeType("JSAPI");

            WxPayMpOrderResult result = wxPayService.createOrder(orderRequest);

            // 构建支付响应
            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(outTradeNo);
            response.setOrderId(request.getOrderId());
            response.setAmount(request.getAmount());
            response.setStatus(PaymentStatus.PENDING);
            response.setPaymentUrl(result.getPackageValue());

            return response;
        } catch (WxPayException e) {
            PaymentResponse response = new PaymentResponse();
            response.setOrderId(request.getOrderId());
            response.setAmount(request.getAmount());
            response.setStatus(PaymentStatus.FAILURE);
            response.setErrorMessage(e.getErrCodeDes());
            return response;
        }*/
    }

    override fun queryPayment(paymentId: String): PaymentResponse {
        throw MicroHttpException(500, "Not implemented")
        /*try {
            com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult result = wxPayService.queryOrder(null, paymentId);

            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(paymentId);
            response.setOrderId(result.getOutTradeNo());
            response.setAmount(new java.math.BigDecimal(result.getTotalFee()).divide(new java.math.BigDecimal("100"))); // 转换为元

            switch (result.getTradeState()) {
                case SUCCESS:
                    response.setStatus(PaymentStatus.SUCCESS);
                    response.setTransactionId(result.getTransactionId());
                    response.setPaidTime(LocalDateTime.parse(result.getTimeEnd(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                    break;
                case REFUND:
                    response.setStatus(PaymentStatus.REFUNDED);
                    break;
                case NOTPAY:
                    response.setStatus(PaymentStatus.PENDING);
                    break;
                case CLOSED:
                    response.setStatus(PaymentStatus.CANCELLED);
                    break;
                case REVOKED:
                    response.setStatus(PaymentStatus.CANCELLED);
                    break;
                case USERPAYING:
                    response.setStatus(PaymentStatus.PENDING);
                    break;
                case PAYERROR:
                    response.setStatus(PaymentStatus.FAILURE);
                    response.setErrorMessage(result.getTradeStateDesc());
                    break;
                default:
                    response.setStatus(PaymentStatus.FAILURE);
                    response.setErrorMessage("Unknown payment status: " + result.getTradeState());
            }

            return response;
        } catch (WxPayException e) {
            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(paymentId);
            response.setStatus(PaymentStatus.FAILURE);
            response.setErrorMessage(e.getErrCodeDes());
            return response;
        }*/
    }

    override fun cancelPayment(paymentId: String): PaymentResponse {
        throw MicroHttpException(500, "Not implemented")
        /*try {
            com.github.binarywang.wxpay.bean.result.WxPayOrderCloseResult result = wxPayService.closeOrder(paymentId);

            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(paymentId);
            response.setStatus(PaymentStatus.CANCELLED);

            return response;
        } catch (WxPayException e) {
            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(paymentId);
            response.setStatus(PaymentStatus.FAILURE);
            response.setErrorMessage(e.getErrCodeDes());
            return response;
        }*/
    }

    override fun handlePaymentNotify(notifyData: String): String {
        throw MicroHttpException(500, "Not implemented")
        /*try {
            com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(notifyData);
            // 这里应该更新数据库中的订单状态
            return wxPayService.getOrderNotifySuccessResponse();
        } catch (WxPayException e) {
            return e.getReturnMsg();
        }*/
    }

    override fun refund(notifyData: String): String {
        return ""
    }

    override fun handleRefundNotify(notifyData: String): String {
        return ""
    }
}