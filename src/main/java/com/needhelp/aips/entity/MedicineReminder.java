package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalTime;

/**
 * 用药提醒表（medicine_reminders）。
 */
@Entity
@Table(name = "medicine_reminders")
public class MedicineReminder extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "medicine_id", nullable = false)
    private Long medicineId;

    @Column(name = "medicine_name", nullable = false, length = 100)
    private String medicineName;

    @Column(length = 100)
    private String dosage;

    @Column(name = "remind_time", nullable = false)
    private LocalTime remindTime;

    @Column(name = "repeat_days", length = 20)
    private String repeatDays = "1,2,3,4,5,6,7";

    @Column(name = "is_enabled", nullable = false)
    private Integer isEnabled = 1; // 1是 0否

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public LocalTime getRemindTime() { return remindTime; }
    public void setRemindTime(LocalTime remindTime) { this.remindTime = remindTime; }
    public String getRepeatDays() { return repeatDays; }
    public void setRepeatDays(String repeatDays) { this.repeatDays = repeatDays; }
    public Integer getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Integer isEnabled) { this.isEnabled = isEnabled; }
}
