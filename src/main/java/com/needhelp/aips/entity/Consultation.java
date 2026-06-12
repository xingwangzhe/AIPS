package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

/**
 * 咨询会话表（consultations）。
 * status: 1进行中 2已结束 3已转人工
 */
@Entity
@Table(name = "consultations")
public class Consultation extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_title", length = 100)
    private String sessionTitle;

    @Column(nullable = false)
    private Integer status = 1; // 1进行中 2已结束 3已转人工

    @Column(name = "is_transferred", nullable = false)
    private Integer isTransferred = 0; // 0否 1是

    @Column(name = "pharmacist_id")
    private Long pharmacistId;

    @Column(name = "satisfaction_rating")
    private Integer satisfactionRating; // 1-5

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getIsTransferred() { return isTransferred; }
    public void setIsTransferred(Integer isTransferred) { this.isTransferred = isTransferred; }
    public Long getPharmacistId() { return pharmacistId; }
    public void setPharmacistId(Long pharmacistId) { this.pharmacistId = pharmacistId; }
    public Integer getSatisfactionRating() { return satisfactionRating; }
    public void setSatisfactionRating(Integer satisfactionRating) { this.satisfactionRating = satisfactionRating; }
}
