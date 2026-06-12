package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录表（payments）。
 * 状态: 0待支付 1支付成功 2支付失败 3已退款
 */
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_method", nullable = false)
    private Integer paymentMethod; // 1微信 2支付宝 3银行卡 4医保

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "third_party_trade_no", length = 64)
    private String thirdPartyTradeNo;

    @Column(nullable = false)
    private Integer status = 0; // 0待支付 1成功 2失败 3已退款

    @Column(name = "paid_time")
    private LocalDateTime paidTime;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Integer getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Integer paymentMethod) { this.paymentMethod = paymentMethod; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getThirdPartyTradeNo() { return thirdPartyTradeNo; }
    public void setThirdPartyTradeNo(String thirdPartyTradeNo) { this.thirdPartyTradeNo = thirdPartyTradeNo; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getPaidTime() { return paidTime; }
    public void setPaidTime(LocalDateTime paidTime) { this.paidTime = paidTime; }
}
