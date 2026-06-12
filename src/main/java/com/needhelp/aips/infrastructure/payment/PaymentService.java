package com.needhelp.aips.infrastructure.payment;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 支付中心服务（空壳）。
 * TODO: 对接微信支付 / 支付宝 / 银行卡 / 医保支付渠道。
 */
@Service
public class PaymentService {

    /**
     * 发起支付。
     * @return 第三方支付流水号
     * TODO: 实际对接支付渠道 SDK
     */
    public String pay(Long orderId, BigDecimal amount, Integer paymentMethod) {
        // TODO: 对接微信支付/支付宝/银行卡/医保
        return "PAY" + System.currentTimeMillis();
    }

    /**
     * 处理支付回调。
     * TODO: 验证回调签名，更新支付状态
     */
    public void handleCallback(String thirdPartyTradeNo, boolean success) {
        // TODO: 处理支付回调
    }

    /**
     * 发起退款。
     * TODO: 对接支付渠道退款接口
     */
    public void refund(Long orderId, BigDecimal amount) {
        // TODO: 退款处理
    }
}
