package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

/**
 * 咨询消息表（consultation_messages）。
 * sender_type: 1用户 2AI 3人工药师
 * msg_type: 1文本 2图片 3语音 4结构化卡片
 * risk_level: 1低 2中 3高
 */
@Entity
@Table(name = "consultation_messages")
public class ConsultationMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @Column(name = "sender_type", nullable = false)
    private Integer senderType; // 1用户 2AI 3人工药师

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "msg_type", nullable = false)
    private Integer msgType = 1; // 1文本 2图片 3语音 4结构化卡片

    @Column(name = "risk_level")
    private Integer riskLevel; // 1低 2中 3高

    @Column(name = "disclaimer_shown", nullable = false)
    private Integer disclaimerShown = 0; // 0否 1是

    @Column(name = "is_reviewed", nullable = false)
    private Integer isReviewed = 0; // 0否 1是

    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
    public Integer getSenderType() { return senderType; }
    public void setSenderType(Integer senderType) { this.senderType = senderType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getMsgType() { return msgType; }
    public void setMsgType(Integer msgType) { this.msgType = msgType; }
    public Integer getRiskLevel() { return riskLevel; }
    public void setRiskLevel(Integer riskLevel) { this.riskLevel = riskLevel; }
    public Integer getDisclaimerShown() { return disclaimerShown; }
    public void setDisclaimerShown(Integer disclaimerShown) { this.disclaimerShown = disclaimerShown; }
    public Integer getIsReviewed() { return isReviewed; }
    public void setIsReviewed(Integer isReviewed) { this.isReviewed = isReviewed; }
}
