package com.needhelp.aips.dto.prescription;

import java.util.List;

public class PrescriptionStatusResponse {
    private Long prescriptionId;
    private String imageUrl;
    private String hospitalName;
    private String doctorName;
    private Integer status;
    private List<PrescriptionItemInfo> items;
    private String pharmacistComment;

    public static class PrescriptionItemInfo {
        private String medicineName;
        private String specification;
        private String dosage;
        private Integer quantity;
        public String getMedicineName() { return medicineName; }
        public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
        public String getSpecification() { return specification; }
        public void setSpecification(String specification) { this.specification = specification; }
        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<PrescriptionItemInfo> getItems() { return items; }
    public void setItems(List<PrescriptionItemInfo> items) { this.items = items; }
    public String getPharmacistComment() { return pharmacistComment; }
    public void setPharmacistComment(String pharmacistComment) { this.pharmacistComment = pharmacistComment; }
}
