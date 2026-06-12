package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

/**
 * 购物车表（cart_items）。
 */
@Entity
@Table(name = "cart_items")
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "is_selected", nullable = false)
    private Integer isSelected = 1; // 1是 0否

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getIsSelected() { return isSelected; }
    public void setIsSelected(Integer isSelected) { this.isSelected = isSelected; }
}
