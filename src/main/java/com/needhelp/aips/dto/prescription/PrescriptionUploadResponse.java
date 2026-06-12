package com.needhelp.aips.dto.prescription;

public class PrescriptionUploadResponse {
    private Long prescriptionId;
    private String imageUrl;
    private Integer ocrStatus;
    private String message;

    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getOcrStatus() { return ocrStatus; }
    public void setOcrStatus(Integer ocrStatus) { this.ocrStatus = ocrStatus; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
