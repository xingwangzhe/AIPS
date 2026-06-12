package com.needhelp.aips.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReminderRequest {
    @NotNull(message = "药品ID不能为空")
    private Long medicineId;

    @NotBlank(message = "药品名称不能为空")
    private String medicineName;

    private String dosage;

    @NotBlank(message = "提醒时间不能为空")
    private String remindTime; // HH:mm

    private String repeatDays = "1,2,3,4,5,6,7";
    private Integer isEnabled = 1;

    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getRemindTime() { return remindTime; }
    public void setRemindTime(String remindTime) { this.remindTime = remindTime; }
    public String getRepeatDays() { return repeatDays; }
    public void setRepeatDays(String repeatDays) { this.repeatDays = repeatDays; }
    public Integer getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Integer isEnabled) { this.isEnabled = isEnabled; }
}
