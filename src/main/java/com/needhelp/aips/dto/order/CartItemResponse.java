package com.needhelp.aips.dto.order;

import java.math.BigDecimal;

public class CartItemResponse {
    private Long cartItemId;
    private String medicineName;
    private Integer quantity;
    private BigDecimal subtotal;
    private Long cartTotalCount;

    public Long getCartItemId() { return cartItemId; }
    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public Long getCartTotalCount() { return cartTotalCount; }
    public void setCartTotalCount(Long cartTotalCount) { this.cartTotalCount = cartTotalCount; }
}
