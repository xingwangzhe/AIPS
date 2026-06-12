package com.needhelp.aips.dto.consult;

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

    public static class AiMsg {
        private String content;
        private String symptomAnalysis;
        private String medicineAdvice;
        private String warnings;
        private Integer riskLevel;
        private String disclaimer;

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
    }

    public UserMsg getUserMessage() { return userMessage; }
    public void setUserMessage(UserMsg userMessage) { this.userMessage = userMessage; }
    public AiMsg getAiReply() { return aiReply; }
    public void setAiReply(AiMsg aiReply) { this.aiReply = aiReply; }
}
