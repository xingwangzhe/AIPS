package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * 订单表（orders）。
 * 状态: 10待付款 20待发货 30配送中 40已完成 50已取消
 */
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "order_no", nullable = false, unique = true, length = 32)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "delivery_type", nullable = false)
    private Integer deliveryType; // 1标准快递 2当日达 3次日达

    @Column(name = "payment_method")
    private Integer paymentMethod; // 1微信 2支付宝 3银行卡 4医保

    @Column(name = "subtotal_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private Integer status = 10; // 10待付款 20待发货 30配送中 40已完成 50已取消

    @Column(name = "is_privacy_pack", nullable = false)
    private Integer isPrivacyPack = 0; // 0否 1是

    @Column(length = 255)
    private String remark;

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    public Integer getDeliveryType() { return deliveryType; }
    public void setDeliveryType(Integer deliveryType) { this.deliveryType = deliveryType; }
    public Integer getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Integer paymentMethod) { this.paymentMethod = paymentMethod; }
    public BigDecimal getSubtotalAmount() { return subtotalAmount; }
    public void setSubtotalAmount(BigDecimal subtotalAmount) { this.subtotalAmount = subtotalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getIsPrivacyPack() { return isPrivacyPack; }
    public void setIsPrivacyPack(Integer isPrivacyPack) { this.isPrivacyPack = isPrivacyPack; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
