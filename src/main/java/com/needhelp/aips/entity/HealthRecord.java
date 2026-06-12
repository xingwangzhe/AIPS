package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康记录表（health_records）。
 * record_type: 1血压 2血糖 3体重 4体温
 */
@Entity
@Table(name = "health_records")
public class HealthRecord extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "record_type", nullable = false)
    private Integer recordType; // 1血压 2血糖 3体重 4体温

    private Integer systolic;   // 收缩压
    private Integer diastolic;  // 舒张压

    @Column(name = "blood_sugar", precision = 4, scale = 1)
    private BigDecimal bloodSugar;

    @Column(name = "sugar_type")
    private Integer sugarType; // 1空腹 2餐后

    @Column(precision = 5, scale = 2)
    private BigDecimal weight; // kg

    @Column(precision = 4, scale = 1)
    private BigDecimal temperature; // ℃

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getRecordType() { return recordType; }
    public void setRecordType(Integer recordType) { this.recordType = recordType; }
    public Integer getSystolic() { return systolic; }
    public void setSystolic(Integer systolic) { this.systolic = systolic; }
    public Integer getDiastolic() { return diastolic; }
    public void setDiastolic(Integer diastolic) { this.diastolic = diastolic; }
    public BigDecimal getBloodSugar() { return bloodSugar; }
    public void setBloodSugar(BigDecimal bloodSugar) { this.bloodSugar = bloodSugar; }
    public Integer getSugarType() { return sugarType; }
    public void setSugarType(Integer sugarType) { this.sugarType = sugarType; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public LocalDateTime getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDateTime recordDate) { this.recordDate = recordDate; }
}
