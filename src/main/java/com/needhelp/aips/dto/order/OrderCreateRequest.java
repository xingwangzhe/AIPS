package com.needhelp.aips.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderCreateRequest {
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    @NotNull(message = "配送方式不能为空")
    private Integer deliveryType; // 1标准快递 2当日达 3次日达

    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod; // 1微信 2支付宝 3银行卡 4医保

    private Integer isPrivacyPack = 0;

    @NotEmpty(message = "购物车项不能为空")
    private List<Long> cartItemIds;

    private String remark;

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    public Integer getDeliveryType() { return deliveryType; }
    public void setDeliveryType(Integer deliveryType) { this.deliveryType = deliveryType; }
    public Integer getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Integer paymentMethod) { this.paymentMethod = paymentMethod; }
    public Integer getIsPrivacyPack() { return isPrivacyPack; }
    public void setIsPrivacyPack(Integer isPrivacyPack) { this.isPrivacyPack = isPrivacyPack; }
    public List<Long> getCartItemIds() { return cartItemIds; }
    public void setCartItemIds(List<Long> cartItemIds) { this.cartItemIds = cartItemIds; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
