package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

/**
 * 处方明细表（prescription_items）。
 */
@Entity
@Table(name = "prescription_items")
public class PrescriptionItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(name = "medicine_name", nullable = false, length = 100)
    private String medicineName;

    @Column(length = 100)
    private String specification;

    @Column(length = 100)
    private String dosage;

    private Integer quantity;

    @Column(name = "ai_check_result")
    private Integer aiCheckResult; // 1通过 2警告 3严重

    @Column(name = "matched_medicine_id")
    private Long matchedMedicineId;

    public Prescription getPrescription() { return prescription; }
    public void setPrescription(Prescription prescription) { this.prescription = prescription; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getAiCheckResult() { return aiCheckResult; }
    public void setAiCheckResult(Integer aiCheckResult) { this.aiCheckResult = aiCheckResult; }
    public Long getMatchedMedicineId() { return matchedMedicineId; }
    public void setMatchedMedicineId(Long matchedMedicineId) { this.matchedMedicineId = matchedMedicineId; }
}
