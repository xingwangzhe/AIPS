package com.needhelp.aips.dto.consult;

import java.math.BigDecimal;
import java.util.List;

public class AiReplyResponse {
    private UserMsg userMessage;
    private AiMsg aiReply;

    public static class UserMsg {
        private Long id;
        private String content;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class MedicineCard {
        private Long id;
        private String name;
        private String specification;
        private BigDecimal price;
        private Integer isPrescription;
        private Integer stock;
        private String indications;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSpecification() { return specification; }
        public void setSpecification(String specification) { this.specification = specification; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getIsPrescription() { return isPrescription; }
        public void setIsPrescription(Integer isPrescription) { this.isPrescription = isPrescription; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        public String getIndications() { return indications; }
        public void setIndications(String indications) { this.indications = indications; }
    }

    public static class AiMsg {
        private String content;
        private String symptomAnalysis;
        private String medicineAdvice;
        private String warnings;
        private Integer riskLevel;
        private String disclaimer;
        private List<MedicineCard> medicines;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSymptomAnalysis() { return symptomAnalysis; }
        public void setSymptomAnalysis(String symptomAnalysis) { this.symptomAnalysis = symptomAnalysis; }
        public String getMedicineAdvice() { return medicineAdvice; }
        public void setMedicineAdvice(String medicineAdvice) { this.medicineAdvice = medicineAdvice; }
        public String getWarnings() { return warnings; }
        public void setWarnings(String warnings) { this.warnings = warnings; }
        public Integer getRiskLevel() { return riskLevel; }
        public void setRiskLevel(Integer riskLevel) { this.riskLevel = riskLevel; }
        public String getDisclaimer() { return disclaimer; }
        public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
        public List<MedicineCard> getMedicines() { return medicines; }
        public void setMedicines(List<MedicineCard> medicines) { this.medicines = medicines; }
    }

    public UserMsg getUserMessage() { return userMessage; }
    public void setUserMessage(UserMsg userMessage) { this.userMessage = userMessage; }
    public AiMsg getAiReply() { return aiReply; }
    public void setAiReply(AiMsg aiReply) { this.aiReply = aiReply; }
}
