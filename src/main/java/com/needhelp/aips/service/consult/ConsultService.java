package com.needhelp.aips.service.consult;

import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.dto.consult.*;
import com.needhelp.aips.entity.Consultation;
import com.needhelp.aips.entity.ConsultationMessage;
import com.needhelp.aips.entity.Medicine;
import com.needhelp.aips.repository.ConsultationMessageRepository;
import com.needhelp.aips.repository.ConsultationRepository;
import com.needhelp.aips.repository.MedicineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智能咨询服务。
 * 使用 Spring AI ChatClient + 数据库 RAG 检索增强。
 */
@Service
public class ConsultService {

    private static final Logger log = LoggerFactory.getLogger(ConsultService.class);

    private static final String SYSTEM_PROMPT = """
            你是一名专业的AI药师助手，拥有丰富的药学知识。你的职责是：
            1. 根据用户描述的症状，提供初步的症状分析
            2. 优先推荐系统中已有的药品（会在上下文中提供），说明用法用量和价格
            3. 明确指出需要就医的警示信号
            4. 对于处方药相关问题，提醒用户必须在医生指导下使用

            请始终以 JSON 格式回复，包含以下字段：
            {
              "content": "完整回复内容",
              "symptomAnalysis": "症状分析",
              "medicineAdvice": "用药建议（提及药品名称和价格）",
              "warnings": "注意事项和警告",
              "riskLevel": 1
            }

            riskLevel 定义：
            - 1（低风险）：常规OTC用药咨询，可直接回复
            - 2（中风险）：涉及处方药或慢病用药，需标注"请遵医嘱"
            - 3（高风险）：涉及糖尿病/心脏病/精神类等严重疾病用药，必须转人工药师

            每条回复末尾必须附加免责声明："本建议仅供参考，请遵医嘱或咨询专业药师。"
            """;

    private final ConsultationRepository consultationRepository;
    private final ConsultationMessageRepository messageRepository;
    private final MedicineRepository medicineRepository;
    private final ChatClient chatClient;

    public ConsultService(ConsultationRepository consultationRepository,
                          ConsultationMessageRepository messageRepository,
                          MedicineRepository medicineRepository,
                          ChatClient.Builder chatClientBuilder) {
        this.consultationRepository = consultationRepository;
        this.messageRepository = messageRepository;
        this.medicineRepository = medicineRepository;
        this.chatClient = chatClientBuilder.build();
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
     * 先从数据库检索相关药品作为上下文，再调用 LLM 生成结构化回复。
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

        // ==================== RAG 检索增强 ====================
        // 从数据库搜索匹配的药品作为 LLM 上下文
        String drugContext = searchDrugContext(content);
        log.debug("RAG 检索到 {} 个相关药品上下文", drugContext.isEmpty() ? 0 : ">0");

        // ==================== 调用 Spring AI ChatClient ====================
        AiReplyResponse.AiMsg aiMsg;
        try {
            String aiResponse = chatClient.prompt()
                    .system(s -> s.text(SYSTEM_PROMPT))
                    .user(u -> u.text("""
                            【系统中匹配到的药品信息】
                            %s

                            【用户问题】
                            %s
                            """.formatted(
                                drugContext.isEmpty() ? "未匹配到相关药品，请基于通用药学知识回答。" : drugContext,
                                content
                            )))
                    .call()
                    .content();

            log.debug("LLM 原始回复: {}", aiResponse);
            aiMsg = parseStructuredReply(aiResponse);

        } catch (Exception e) {
            log.error("LLM 调用失败: {}", e.getMessage());
            aiMsg = fallbackReply(content);
        }

        // 高风险回答不展示 AI 内容
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

        AiReplyResponse resp = new AiReplyResponse();
        AiReplyResponse.UserMsg uMsg = new AiReplyResponse.UserMsg();
        uMsg.setId(userMsg.getId());
        uMsg.setContent(userMsg.getContent());
        resp.setUserMessage(uMsg);
        resp.setAiReply(aiMsg);

        return resp;
    }

    /**
     * 从数据库中搜索与用户问题匹配的药品，构建上下文文本。
     */
    private String searchDrugContext(String userMessage) {
        try {
            List<Medicine> medicines = medicineRepository
                    .searchByKeyword(userMessage, PageRequest.of(0, 5))
                    .getContent();

            if (medicines.isEmpty()) {
                return "";
            }

            return medicines.stream()
                    .map(m -> String.format("- %s【%s】%s · ￥%.2f · %s · 适应症：%s",
                            m.getName(),
                            m.getIsPrescription() == 1 ? "RX处方药" : "OTC",
                            m.getSpecification(),
                            m.getPrice(),
                            m.getStock() > 0 ? "库存" + m.getStock() : "缺货",
                            m.getIndications() != null ? m.getIndications() : "暂无"))
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.warn("药品检索失败: {}", e.getMessage());
            return "";
        }
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

    // ==================== 响应解析 ====================

    private AiReplyResponse.AiMsg parseStructuredReply(String content) {
        AiReplyResponse.AiMsg ai = new AiReplyResponse.AiMsg();
        ai.setDisclaimer("本建议仅供参考，请遵医嘱或咨询专业药师。如有严重不适，请立即就医。");

        String json = extractJson(content);

        if (json != null) {
            ai.setContent(extractStringField(json, "content", content));
            ai.setSymptomAnalysis(extractStringField(json, "symptomAnalysis", ""));
            ai.setMedicineAdvice(extractStringField(json, "medicineAdvice", ""));
            ai.setWarnings(extractStringField(json, "warnings", ""));
            int riskLevel = extractIntField(json, "riskLevel", 1);
            ai.setRiskLevel(Math.min(Math.max(riskLevel, 1), 3));
        } else {
            ai.setContent(content);
            ai.setRiskLevel(1);
        }
        return ai;
    }

    private String extractJson(String content) {
        if (content == null) return null;
        int start = content.indexOf("```json");
        if (start >= 0) {
            start = content.indexOf('\n', start) + 1;
            int end = content.indexOf("```", start);
            if (end > start) return content.substring(start, end).trim();
        }
        start = content.indexOf("```");
        if (start >= 0) {
            start = content.indexOf('\n', start) + 1;
            int end = content.indexOf("```", start);
            if (end > start) return content.substring(start, end).trim();
        }
        if (content.trim().startsWith("{")) return content.trim();
        return null;
    }

    private String extractStringField(String json, String fieldName, String defaultVal) {
        String key = "\"" + fieldName + "\"";
        int keyIdx = json.indexOf(key);
        if (keyIdx < 0) return defaultVal;
        int colonIdx = json.indexOf(':', keyIdx);
        if (colonIdx < 0) return defaultVal;
        int valStart = json.indexOf('"', colonIdx + 1);
        if (valStart < 0) return defaultVal;
        int valEnd = json.indexOf('"', valStart + 1);
        if (valEnd < 0) return defaultVal;
        return json.substring(valStart + 1, valEnd)
                .replace("\\\"", "\"").replace("\\n", "\n").replace("\\t", "\t");
    }

    private int extractIntField(String json, String fieldName, int defaultVal) {
        String key = "\"" + fieldName + "\"";
        int keyIdx = json.indexOf(key);
        if (keyIdx < 0) return defaultVal;
        int colonIdx = json.indexOf(':', keyIdx);
        if (colonIdx < 0) return defaultVal;
        int valStart = colonIdx + 1;
        while (valStart < json.length() && Character.isWhitespace(json.charAt(valStart))) valStart++;
        StringBuilder num = new StringBuilder();
        while (valStart < json.length() && Character.isDigit(json.charAt(valStart))) {
            num.append(json.charAt(valStart));
            valStart++;
        }
        if (num.isEmpty()) return defaultVal;
        try { return Integer.parseInt(num.toString()); } catch (NumberFormatException e) { return defaultVal; }
    }

    // ==================== 降级方案 ====================

    private AiReplyResponse.AiMsg fallbackReply(String content) {
        AiReplyResponse.AiMsg ai = new AiReplyResponse.AiMsg();
        String lower = content.toLowerCase();

        if (lower.contains("头痛") || lower.contains("发烧") || lower.contains("感冒")) {
            ai.setContent("根据您的描述，头痛伴发烧可能是感冒引起的。");
            ai.setSymptomAnalysis("头痛+发热，疑似感冒前驱症状");
            ai.setMedicineAdvice("系统中：布洛芬缓释胶囊（¥19.90）、对乙酰氨基酚片（¥12.50）");
            ai.setWarnings("如症状持续超过3天或高烧超过38.5℃，请及时就医");
            ai.setRiskLevel(1);
        } else if (lower.contains("胃痛") || lower.contains("腹泻") || lower.contains("消化不良")) {
            ai.setContent("您描述的是消化系统不适症状。");
            ai.setSymptomAnalysis("腹痛/腹泻，疑似消化系统功能紊乱");
            ai.setMedicineAdvice("系统中：蒙脱石散（¥15.50）、奥美拉唑肠溶胶囊（¥28.00）");
            ai.setWarnings("如出现剧烈腹痛、持续呕吐或便血，请立即就医");
            ai.setRiskLevel(1);
        } else {
            ai.setContent("感谢您的咨询。建议提供更多症状信息，或转接人工药师获取专业帮助。");
            ai.setSymptomAnalysis("信息不足");
            ai.setMedicineAdvice("");
            ai.setWarnings("如症状持续，请及时就医");
            ai.setRiskLevel(1);
        }
        ai.setDisclaimer("本建议仅供参考，请遵医嘱或咨询专业药师。如有严重不适，请立即就医。");
        return ai;
    }
}
