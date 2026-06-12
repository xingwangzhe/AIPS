package com.needhelp.aips.dto.user;

import java.math.BigDecimal;
import java.util.List;

public class HealthProfileResponse {
    private String allergyInfo;
    private String chronicDiseases;
    private List<CurrentMedication> currentMedications;
    private BloodPressure latestBloodPressure;
    private BloodGlucose latestBloodGlucose;
    private List<FamilyMemberInfo> familyMembers;

    public static class CurrentMedication {
        private String name;
        private String dosage;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }
    }

    public static class BloodPressure {
        private Integer systolic;
        private Integer diastolic;
        public Integer getSystolic() { return systolic; }
        public void setSystolic(Integer systolic) { this.systolic = systolic; }
        public Integer getDiastolic() { return diastolic; }
        public void setDiastolic(Integer diastolic) { this.diastolic = diastolic; }
    }

    public static class BloodGlucose {
        private BigDecimal value;
        private String type;
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class FamilyMemberInfo {
        private String name;
        private String relationship;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }
    }

    // getters/setters
    public String getAllergyInfo() { return allergyInfo; }
    public void setAllergyInfo(String allergyInfo) { this.allergyInfo = allergyInfo; }
    public String getChronicDiseases() { return chronicDiseases; }
    public void setChronicDiseases(String chronicDiseases) { this.chronicDiseases = chronicDiseases; }
    public List<CurrentMedication> getCurrentMedications() { return currentMedications; }
    public void setCurrentMedications(List<CurrentMedication> currentMedications) { this.currentMedications = currentMedications; }
    public BloodPressure getLatestBloodPressure() { return latestBloodPressure; }
    public void setLatestBloodPressure(BloodPressure latestBloodPressure) { this.latestBloodPressure = latestBloodPressure; }
    public BloodGlucose getLatestBloodGlucose() { return latestBloodGlucose; }
    public void setLatestBloodGlucose(BloodGlucose latestBloodGlucose) { this.latestBloodGlucose = latestBloodGlucose; }
    public List<FamilyMemberInfo> getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(List<FamilyMemberInfo> familyMembers) { this.familyMembers = familyMembers; }
}
