package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * 家庭成员表（family_members）。
 */
@Entity
@Table(name = "family_members")
public class FamilyMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer relationship; // 1配偶 2父亲 3母亲 4儿子 5女儿 6其他

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private Integer gender; // 1男 2女

    @Column(name = "allergy_info", length = 500)
    private String allergyInfo;

    @Column(name = "chronic_diseases", length = 500)
    private String chronicDiseases;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getRelationship() { return relationship; }
    public void setRelationship(Integer relationship) { this.relationship = relationship; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getAllergyInfo() { return allergyInfo; }
    public void setAllergyInfo(String allergyInfo) { this.allergyInfo = allergyInfo; }
    public String getChronicDiseases() { return chronicDiseases; }
    public void setChronicDiseases(String chronicDiseases) { this.chronicDiseases = chronicDiseases; }
}
