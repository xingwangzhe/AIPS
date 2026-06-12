package com.needhelp.aips.service.consult;

import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.dto.consult.*;
import com.needhelp.aips.entity.Consultation;
import com.needhelp.aips.entity.ConsultationMessage;
import com.needhelp.aips.entity.Medicine;
import com.needhelp.aips.infrastructure.ai.EmbeddingService;
import com.needhelp.aips.repository.*;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultService {

    private static final Logger log = LoggerFactory.getLogger(ConsultService.class);

    private static final String SYSTEM_PROMPT = """
            你是一名专业 AI 药师助手。根据用户症状进行分析，
            优先推荐系统药品库中匹配到的药品（名称/价格/库存/适应症）。
            处方药标注"请遵医嘱"，严格以 JSON 格式回复：
            {"content":"完整回复","symptomAnalysis":"症状分析","medicineAdvice":"用药建议（含药品名称和价格）","warnings":"注意事项","riskLevel":1}
            riskLevel: 1=低风险OTC 2=中风险(处方药) 3=高风险(转人工)
            回复末尾附："本建议仅供参考，请遵医嘱或咨询专业药师。"
            """;

    private final ConsultationRepository consultationRepository;
    private final ConsultationMessageRepository messageRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineVectorRepository vectorRepo;
    private final EmbeddingService embeddingService;
    private final ChatModel chatModel;

    public ConsultService(ConsultationRepository consultationRepository,
                          ConsultationMessageRepository messageRepository,
                          MedicineRepository medicineRepository,
                          MedicineVectorRepository vectorRepo,
                          EmbeddingService embeddingService,
                          ChatModel chatModel) {
        this.consultationRepository = consultationRepository;
        this.messageRepository = messageRepository;
        this.medicineRepository = medicineRepository;
        this.vectorRepo = vectorRepo;
        this.embeddingService = embeddingService;
        this.chatModel = chatModel;
    }

    /** 启动时自动索引未索引药品的向量 */
    @PostConstruct
    void indexMedicines() {
        long count = vectorRepo.countUnindexed();
        if (count == 0) return;
        log.info("开始向量索引 {} 个药品...", count);
        List<Medicine> all = medicineRepository.findAll();
        for (Medicine m : all) {
            if (vectorRepo.hasEmbedding(m.getId())) continue;
            String text = m.getName() + " " + m.getGenericName() + " " + m.getIndications();
            float[] vec = embeddingService.embed(text);
            if (vec.length > 0) vectorRepo.saveEmbedding(m.getId(), vec);
        }
        log.info("药品向量索引完成");
    }

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

    @Transactional
    public AiReplyResponse sendMessage(Long userId, Long sessionId, String content, Integer msgType) {
        Consultation session = consultationRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在"));
        if (!session.getUserId().equals(userId))
            throw new BusinessException(403, "无权访问该会话");
        if (session.getStatus() != 1)
            throw new BusinessException(400, "会话已结束或已转人工");

        ConsultationMessage userMsg = new ConsultationMessage();
        userMsg.setConsultation(session);
        userMsg.setSenderType(1);
        userMsg.setContent(content);
        userMsg.setMsgType(msgType != null ? msgType : 1);
        messageRepository.save(userMsg);

        // ===== pgvector 语义检索 =====
        String drugContext = searchDrugsVector(content);
        log.info("Vector RAG: matched={}", !drugContext.isEmpty());

        // ===== LLM 调用 =====
        AiReplyResponse.AiMsg aiMsg;
        try {
            String userPrompt = drugContext.isEmpty()
                    ? content
                    : "【药品库匹配结果】\n" + drugContext + "\n\n【用户问题】\n" + content;

            String reply = chatModel.chat(
                    ChatRequest.builder()
                            .messages(List.of(
                                    SystemMessage.from(SYSTEM_PROMPT),
                                    UserMessage.from(userPrompt)
                            ))
                            .build()
            ).aiMessage().text();

            log.info("LLM 回复 length={}", reply.length());
            aiMsg = parseStructuredReply(reply);

        } catch (Exception e) {
            log.error("LLM 调用失败: {}", e.toString());
            // 降级：仍搜数据库给出真实药品推荐
            aiMsg = fallbackWithDrugs(content, drugContext);
        }

        if (aiMsg.getRiskLevel() != null && aiMsg.getRiskLevel() == 3) {
            aiMsg.setContent("该问题涉及高风险用药，AI 无法直接作答。建议转接人工药师。");
            aiMsg.setMedicineAdvice("");
        }

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

    /** pgvector 语义检索 → 降级关键词 */
    private String searchDrugsVector(String query) {
        try {
            float[] vec = embeddingService.embed(query);
            if (vec.length == 0) return searchDrugsKeyword(query);
            List<Long> ids = vectorRepo.searchSimilar(vec, 5);
            if (ids.isEmpty()) return searchDrugsKeyword(query);
            return formatMedicines(medicineRepository.findAllById(ids));
        } catch (Exception e) {
            log.warn("向量检索失败: {}", e.getMessage());
            return "";
        }
    }

    private String searchDrugsKeyword(String keyword) {
        try {
            List<Medicine> meds = medicineRepository
                    .searchByKeyword(keyword, org.springframework.data.domain.PageRequest.of(0, 5))
                    .getContent();
            return meds.isEmpty() ? "" : formatMedicines(meds);
        } catch (Exception e) { return ""; }
    }

    private String formatMedicines(List<Medicine> meds) {
        return meds.stream()
                .map(m -> String.format("- %s【%s】%s · ¥%.2f · %s · 适应症：%s",
                        m.getName(), m.getIsPrescription() == 1 ? "RX" : "OTC",
                        m.getSpecification(), m.getPrice(),
                        m.getStock() > 0 ? "库存" + m.getStock() : "缺货",
                        m.getIndications() != null ? m.getIndications() : "暂无"))
                .collect(Collectors.joining("\n"));
    }

    public TransferResponse transferToHuman(Long userId, Long sessionId, String reason) {
        Consultation session = consultationRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在"));
        if (!session.getUserId().equals(userId))
            throw new BusinessException(403, "无权操作该会话");
        session.setStatus(3);
        session.setIsTransferred(1);
        consultationRepository.save(session);
        TransferResponse resp = new TransferResponse();
        resp.setSessionId(sessionId);
        resp.setStatus("waiting");
        resp.setQueuePosition(1);
        return resp;
    }

    // ========== JSON 解析 ==========

    private AiReplyResponse.AiMsg parseStructuredReply(String content) {
        AiReplyResponse.AiMsg ai = new AiReplyResponse.AiMsg();
        ai.setDisclaimer("本建议仅供参考，请遵医嘱或咨询专业药师。");
        String json = extractJson(content);
        if (json != null) {
            ai.setContent(extractStringField(json, "content", content));
            ai.setSymptomAnalysis(extractStringField(json, "symptomAnalysis", ""));
            ai.setMedicineAdvice(extractStringField(json, "medicineAdvice", ""));
            ai.setWarnings(extractStringField(json, "warnings", ""));
            int r = extractIntField(json, "riskLevel", 1);
            ai.setRiskLevel(Math.min(Math.max(r, 1), 3));
        } else {
            ai.setContent(content);
            ai.setRiskLevel(1);
        }
        return ai;
    }

    private String extractJson(String c) {
        if (c == null) return null;
        int s = c.indexOf("```json"); if (s >= 0) { s = c.indexOf('\n', s) + 1; int e = c.indexOf("```", s); if (e > s) return c.substring(s, e).trim(); }
        s = c.indexOf("```"); if (s >= 0) { s = c.indexOf('\n', s) + 1; int e = c.indexOf("```", s); if (e > s) return c.substring(s, e).trim(); }
        return c.trim().startsWith("{") ? c.trim() : null;
    }

    private String extractStringField(String json, String f, String d) {
        String k = "\"" + f + "\""; int ki = json.indexOf(k); if (ki < 0) return d;
        int ci = json.indexOf(':', ki); if (ci < 0) return d;
        int vs = json.indexOf('"', ci + 1); if (vs < 0) return d;
        int ve = json.indexOf('"', vs + 1); if (ve < 0) return d;
        return json.substring(vs + 1, ve).replace("\\\"", "\"").replace("\\n", "\n");
    }

    private int extractIntField(String json, String f, int d) {
        String k = "\"" + f + "\""; int ki = json.indexOf(k); if (ki < 0) return d;
        int ci = json.indexOf(':', ki); if (ci < 0) return d;
        int vs = ci + 1; while (vs < json.length() && Character.isWhitespace(json.charAt(vs))) vs++;
        StringBuilder n = new StringBuilder();
        while (vs < json.length() && Character.isDigit(json.charAt(vs))) n.append(json.charAt(vs++));
        if (n.isEmpty()) return d;
        try { return Integer.parseInt(n.toString()); } catch (NumberFormatException e) { return d; }
    }

    // ========== 降级（含数据库检索） ==========

    private AiReplyResponse.AiMsg fallbackWithDrugs(String content, String drugContext) {
        AiReplyResponse.AiMsg ai = new AiReplyResponse.AiMsg();
        String l = content.toLowerCase();

        if (!drugContext.isEmpty()) {
            ai.setContent("根据您的症状，系统中有以下匹配药品：\n" + drugContext + "\n\n建议在药师指导下选用。");
            ai.setMedicineAdvice(drugContext);
        } else if (l.contains("头痛") || l.contains("发烧") || l.contains("感冒") || l.contains("脑热")) {
            ai.setContent("头痛发热可能是感冒引起，建议多休息、多喝水。系统中可选用布洛芬缓释胶囊或对乙酰氨基酚片。");
            ai.setMedicineAdvice("布洛芬缓释胶囊(¥19.90) 或 对乙酰氨基酚片(¥12.50)");
        } else if (l.contains("胃痛") || l.contains("腹泻") || l.contains("消化不良")) {
            ai.setContent("消化系统不适，建议清淡饮食。");
            ai.setMedicineAdvice("蒙脱石散(¥15.50) 或 奥美拉唑肠溶胶囊(¥28.00)");
        } else {
            ai.setContent("请详细描述您的症状（如头痛、发烧、咳嗽、胃痛等），以便为您推荐合适的药品。");
        }
        ai.setSymptomAnalysis(l.contains("头痛") || l.contains("发烧") ? "疑似感冒症状" : "信息不足");
        ai.setRiskLevel(1);
        ai.setWarnings("如症状持续或加重，请及时就医。");
        ai.setDisclaimer("本建议仅供参考，请遵医嘱或咨询专业药师。");
        return ai;
    }
}
