package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 处方表（prescriptions）。
 * ocr_status: 0待识别 1识别中 2成功 3失败
 * ai_check_status: 0待检查 1通过 2警告 3严重
 * pharmacist_status: 0待审核 1通过 2驳回 3需重传
 */
@Entity
@Table(name = "prescriptions")
public class Prescription extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "prescription_no", nullable = false, unique = true, length = 32)
    private String prescriptionNo;

    @Column(name = "hospital_name", length = 100)
    private String hospitalName;

    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Column(name = "prescription_date")
    private LocalDate prescriptionDate;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "ocr_status", nullable = false)
    private Integer ocrStatus = 0;

    @Column(name = "ai_check_status", nullable = false)
    private Integer aiCheckStatus = 0;

    @Column(name = "pharmacist_status", nullable = false)
    private Integer pharmacistStatus = 0;

    @Column(name = "pharmacist_id")
    private Long pharmacistId;

    @Column(name = "pharmacist_comment", length = 500)
    private String pharmacistComment;

    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    // getters/setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPrescriptionNo() { return prescriptionNo; }
    public void setPrescriptionNo(String prescriptionNo) { this.prescriptionNo = prescriptionNo; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getOcrStatus() { return ocrStatus; }
    public void setOcrStatus(Integer ocrStatus) { this.ocrStatus = ocrStatus; }
    public Integer getAiCheckStatus() { return aiCheckStatus; }
    public void setAiCheckStatus(Integer aiCheckStatus) { this.aiCheckStatus = aiCheckStatus; }
    public Integer getPharmacistStatus() { return pharmacistStatus; }
    public void setPharmacistStatus(Integer pharmacistStatus) { this.pharmacistStatus = pharmacistStatus; }
    public Long getPharmacistId() { return pharmacistId; }
    public void setPharmacistId(Long pharmacistId) { this.pharmacistId = pharmacistId; }
    public String getPharmacistComment() { return pharmacistComment; }
    public void setPharmacistComment(String pharmacistComment) { this.pharmacistComment = pharmacistComment; }
    public LocalDateTime getReviewTime() { return reviewTime; }
    public void setReviewTime(LocalDateTime reviewTime) { this.reviewTime = reviewTime; }
}
