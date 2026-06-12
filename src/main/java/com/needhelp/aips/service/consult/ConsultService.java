package com.needhelp.aips.service.consult;

import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.dto.consult.*;
import com.needhelp.aips.entity.Consultation;
import com.needhelp.aips.entity.ConsultationMessage;
import com.needhelp.aips.infrastructure.ai.OpenAiClient;
import com.needhelp.aips.repository.ConsultationMessageRepository;
import com.needhelp.aips.repository.ConsultationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 智能咨询服务。
 * 通过 OpenAI API 提供 AI 药师多轮对话，含风险分级与降级容错。
 */
@Service
public class ConsultService {

    private final ConsultationRepository consultationRepository;
    private final ConsultationMessageRepository messageRepository;
    private final OpenAiClient openAiClient;

    public ConsultService(ConsultationRepository consultationRepository,
                          ConsultationMessageRepository messageRepository,
                          OpenAiClient openAiClient) {
        this.consultationRepository = consultationRepository;
        this.messageRepository = messageRepository;
        this.openAiClient = openAiClient;
    }

    /**
     * 创建咨询会话。
     */
    public SessionResponse createSession(Long userId, String title) {
        Consultation session = new Consultation();
        session.setUserId(userId);
        session.setSessionTitle(title != null && !title.isEmpty() ? title : "用药咨询");
        session.setStatus(1);
        session.setIsTransferred(0);
        session = consultationRepository.save(session);

        SessionResponse resp = new SessionResponse();
        resp.setSessionId(session.getId());
        resp.setTitle(session.getSessionTitle());
        resp.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return resp;
    }

    /**
     * 发送咨询消息并获取 AI 回复。
     * 通过 OpenAI API 生成结构化回复；未配置 API Key 时自动降级为关键词模拟。
     */
    @Transactional
    public AiReplyResponse sendMessage(Long userId, Long sessionId, String content, Integer msgType) {
        Consultation session = consultationRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在"));

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问该会话");
        }
        if (session.getStatus() != 1) {
            throw new BusinessException(400, "会话已结束或已转人工");
        }

        // 保存用户消息
        ConsultationMessage userMsg = new ConsultationMessage();
        userMsg.setConsultation(session);
        userMsg.setSenderType(1);
        userMsg.setContent(content);
        userMsg.setMsgType(msgType != null ? msgType : 1);
        messageRepository.save(userMsg);

        // 调用 OpenAI API 生成回复（未配置 Key 时自动降级为模拟回复）
        AiReplyResponse.AiMsg aiMsg = openAiClient.chat(content);

        // 高风险回答不展示 AI 内容，自动弹出转人工提示
        if (aiMsg.getRiskLevel() != null && aiMsg.getRiskLevel() == 3) {
            aiMsg.setContent("您的问题涉及高风险用药，AI 无法直接作答。建议转接人工药师获取专业指导。");
            aiMsg.setMedicineAdvice("");
        }

        // 保存 AI 回复
        ConsultationMessage aiReplyMsg = new ConsultationMessage();
        aiReplyMsg.setConsultation(session);
        aiReplyMsg.setSenderType(2);
        aiReplyMsg.setContent(aiMsg.getContent());
        aiReplyMsg.setMsgType(1);
        aiReplyMsg.setRiskLevel(aiMsg.getRiskLevel());
        aiReplyMsg.setDisclaimerShown(1);
        messageRepository.save(aiReplyMsg);

        // 构建响应
        AiReplyResponse resp = new AiReplyResponse();
        AiReplyResponse.UserMsg uMsg = new AiReplyResponse.UserMsg();
        uMsg.setId(userMsg.getId());
        uMsg.setContent(userMsg.getContent());
        resp.setUserMessage(uMsg);
        resp.setAiReply(aiMsg);

        return resp;
    }

    /**
     * 转接人工药师。
     */
    public TransferResponse transferToHuman(Long userId, Long sessionId, String reason) {
        Consultation session = consultationRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在"));

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该会话");
        }

        session.setStatus(3);
        session.setIsTransferred(1);
        consultationRepository.save(session);

        TransferResponse resp = new TransferResponse();
        resp.setSessionId(sessionId);
        resp.setStatus("waiting");
        resp.setQueuePosition(1);
        return resp;
    }
}
