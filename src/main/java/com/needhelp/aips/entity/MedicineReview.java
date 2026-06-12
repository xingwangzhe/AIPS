package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

/**
 * 药品评价表（medicine_reviews）。
 */
@Entity
@Table(name = "medicine_reviews")
public class MedicineReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(length = 500)
    private String content;

    @Column(name = "is_anonymous", nullable = false)
    private Integer isAnonymous = 0; // 0否 1是

    @Column(nullable = false)
    private Integer status = 1; // 1显示 0隐藏

    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Integer isAnonymous) { this.isAnonymous = isAnonymous; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
