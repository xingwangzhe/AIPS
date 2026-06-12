package com.needhelp.aips.service.consult;

import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.dto.consult.*;
import com.needhelp.aips.entity.Consultation;
import com.needhelp.aips.entity.ConsultationMessage;
import com.needhelp.aips.repository.ConsultationMessageRepository;
import com.needhelp.aips.repository.ConsultationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 智能咨询服务。
 * 处理 AI 咨询会话和消息，包含模拟的风险分级逻辑。
 */
@Service
public class ConsultService {

    private final ConsultationRepository consultationRepository;
    private final ConsultationMessageRepository messageRepository;

    public ConsultService(ConsultationRepository consultationRepository,
                          ConsultationMessageRepository messageRepository) {
        this.consultationRepository = consultationRepository;
        this.messageRepository = messageRepository;
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
     * 使用关键词匹配模拟 AI 回答逻辑。
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

        // 模拟 AI 回答
        AiReplyResponse.AiMsg aiMsg = generateAiReply(content);

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

    /**
     * 模拟 AI 回答生成。
     * 基于关键词匹配，实际生产应接入 LLM。
     */
    private AiReplyResponse.AiMsg generateAiReply(String userContent) {
        AiReplyResponse.AiMsg ai = new AiReplyResponse.AiMsg();

        String lower = userContent.toLowerCase();

        if (lower.contains("头痛") || lower.contains("发烧") || lower.contains("感冒")) {
            ai.setContent("根据您的描述，头痛伴发烧可能是感冒引起的。建议服用解热镇痛类药物，并多休息多喝水。");
            ai.setSymptomAnalysis("头痛+发热，疑似感冒前驱症状");
            ai.setMedicineAdvice("可考虑服用对乙酰氨基酚片（500mg），每日不超过4次");
            ai.setWarnings("如症状持续超过3天或高烧超过38.5℃，请及时就医");
            ai.setRiskLevel(1); // 低风险
        } else if (lower.contains("胃痛") || lower.contains("腹泻") || lower.contains("消化不良")) {
            ai.setContent("您描述的是消化系统不适症状。可能是饮食不当引起的胃肠道功能紊乱。");
            ai.setSymptomAnalysis("腹痛/腹泻，疑似消化系统功能紊乱");
            ai.setMedicineAdvice("可考虑服用蒙脱石散或益生菌制剂，注意清淡饮食");
            ai.setWarnings("如出现剧烈腹痛、持续呕吐或便血，请立即就医");
            ai.setRiskLevel(1);
        } else if (lower.contains("高血压") || lower.contains("血压")) {
            ai.setContent("高血压是常见的慢性病，需要长期规范治疗。建议定期监测血压并遵医嘱服药。");
            ai.setSymptomAnalysis("高血压相关问题咨询");
            ai.setMedicineAdvice("氨氯地平片（5mg每日1次）是常用的一线降压药，请在医生指导下使用");
            ai.setWarnings("处方药需凭医师处方购买，AI建议仅供参考");
            ai.setRiskLevel(2); // 中风险 - 涉及处方药
        } else if (lower.contains("糖尿病") || lower.contains("血糖") || lower.contains("胰岛素")) {
            ai.setContent("糖尿病用药需要严格的医学监督。AI不能替代内分泌科医生的诊断。");
            ai.setSymptomAnalysis("糖尿病相关咨询 - 高风险话题");
            ai.setMedicineAdvice("");
            ai.setWarnings("请务必在医生指导下调整降糖药或胰岛素用量，不可自行更改治疗方案");
            ai.setRiskLevel(3); // 高风险 - 转人工
        } else {
            ai.setContent("感谢您的咨询。您描述的症状我暂时无法做出准确判断，建议您提供更多信息或转接人工药师获取更专业的帮助。");
            ai.setSymptomAnalysis("信息不足，无法进行症状分析");
            ai.setMedicineAdvice("");
            ai.setWarnings("建议详细描述症状，或前往医院就诊");
            ai.setRiskLevel(1);
        }

        ai.setDisclaimer("本建议仅供参考，请遵医嘱或咨询专业药师。如有严重不适，请立即就医。");
        return ai;
    }
}
