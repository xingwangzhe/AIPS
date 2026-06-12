package com.needhelp.aips.service.consult;

import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.dto.consult.*;
import com.needhelp.aips.entity.Consultation;
import com.needhelp.aips.entity.ConsultationMessage;
import com.needhelp.aips.entity.Medicine;
import com.needhelp.aips.infrastructure.ai.EmbeddingService;
import com.needhelp.aips.repository.*;
import dev.langchain4j.data.message.ChatMessage;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsultService {

    private static final Logger log = LoggerFactory.getLogger(ConsultService.class);

    private static final String SYSTEM_PROMPT = """
            你是专业 AI 药师助手。结合对话历史和药品库匹配结果回答问题。
            处方药标注"请遵医嘱"。JSON 格式回复：
            {"content":"完整回复","symptomAnalysis":"症状分析","medicineAdvice":"用药建议","warnings":"注意事项","riskLevel":1}
            riskLevel: 1=低风险OTC 2=中风险(处方药) 3=高风险(转人工)
            末尾附免责声明。
            """;

    private static final int MAX_HISTORY = 10;

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

    @PostConstruct
    void indexMedicines() {
        long count = vectorRepo.countUnindexed();
        if (count == 0) return;
        log.info("开始向量索引 {} 个药品...", count);
        for (Medicine m : medicineRepository.findAll()) {
            if (vectorRepo.hasEmbedding(m.getId())) continue;
            float[] vec = embeddingService.embed(m.getName() + " " + m.getIndications());
            if (vec.length > 0) vectorRepo.saveEmbedding(m.getId(), vec);
        }
        log.info("药品向量索引完成");
    }

    public SessionResponse createSession(Long userId, String title) {
        Consultation s = new Consultation();
        s.setUserId(userId);
        s.setSessionTitle(title != null && !title.isEmpty() ? title : "用药咨询");
        s.setStatus(1);
        s = consultationRepository.save(s);
        SessionResponse r = new SessionResponse();
        r.setSessionId(s.getId()); r.setTitle(s.getSessionTitle());
        r.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return r;
    }

    @Transactional
    public AiReplyResponse sendMessage(Long userId, Long sessionId, String content, Integer msgType) {
        Consultation session = consultationRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在"));
        if (!session.getUserId().equals(userId)) throw new BusinessException(403, "无权访问");
        if (session.getStatus() != 1) throw new BusinessException(400, "会话已结束");

        // 保存用户消息
        ConsultationMessage userMsg = new ConsultationMessage();
        userMsg.setConsultation(session); userMsg.setSenderType(1);
        userMsg.setContent(content); userMsg.setMsgType(msgType != null ? msgType : 1);
        messageRepository.save(userMsg);

        // ===== 加载对话历史 =====
        List<ConsultationMessage> history = messageRepository
                .findByConsultationIdOrderByCreateTimeAsc(sessionId);
        // 只取最近 N 条（不含刚存的这条）
        List<ConsultationMessage> recentHistory = history.size() > MAX_HISTORY + 1
                ? history.subList(history.size() - MAX_HISTORY - 1, history.size() - 1)
                : history.subList(0, Math.max(0, history.size() - 1));

        // ===== RAG: 从历史+当前消息中提取关键词检索药品 =====
        String searchText = buildSearchText(recentHistory, content);
        List<Medicine> matchedMeds = searchDrugsVectorMeds(searchText);
        String drugContext = formatMedicines(matchedMeds);
        log.info("RAG: searchText='{}' matched={}", searchText, matchedMeds.size());

        // ===== 构建对话消息列表 =====
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(SystemMessage.from(SYSTEM_PROMPT));
        // 注入药品上下文
        if (!drugContext.isEmpty()) {
            messages.add(UserMessage.from("【药品库匹配结果，请在回复中引用】\n" + drugContext));
            messages.add(dev.langchain4j.data.message.AiMessage.from("已收到药品库数据，将在回复中引用这些药品。"));
        }
        // 注入历史对话
        for (ConsultationMessage h : recentHistory) {
            if (h.getSenderType() == 1) messages.add(UserMessage.from(h.getContent()));
            else messages.add(dev.langchain4j.data.message.AiMessage.from(
                    h.getContent() != null ? h.getContent() : ""));
        }
        // 当前消息
        messages.add(UserMessage.from(content));

        // ===== LLM 调用 =====
        AiReplyResponse.AiMsg aiMsg;
        try {
            String reply = chatModel.chat(ChatRequest.builder().messages(messages).build())
                    .aiMessage().text();
            log.info("LLM reply length={}", reply != null ? reply.length() : 0);
            aiMsg = parseStructuredReply(reply != null ? reply : content);
        } catch (Exception e) {
            log.error("LLM 调用失败: {}", e.toString());
            aiMsg = fallbackWithContext(searchText, drugContext, recentHistory, content);
        }
        // 注入匹配到的药品卡片供前端渲染
        aiMsg.setMedicines(toMedicineCards(matchedMeds));

        if (aiMsg.getRiskLevel() != null && aiMsg.getRiskLevel() == 3) {
            aiMsg.setContent("该问题涉及高风险用药，建议转接人工药师。");
            aiMsg.setMedicineAdvice("");
        }

        ConsultationMessage aiReplyMsg = new ConsultationMessage();
        aiReplyMsg.setConsultation(session); aiReplyMsg.setSenderType(2);
        aiReplyMsg.setContent(aiMsg.getContent()); aiReplyMsg.setMsgType(1);
        aiReplyMsg.setRiskLevel(aiMsg.getRiskLevel()); aiReplyMsg.setDisclaimerShown(1);
        messageRepository.save(aiReplyMsg);

        AiReplyResponse resp = new AiReplyResponse();
        AiReplyResponse.UserMsg u = new AiReplyResponse.UserMsg();
        u.setId(userMsg.getId()); u.setContent(userMsg.getContent());
        resp.setUserMessage(u); resp.setAiReply(aiMsg);
        return resp;
    }

    /** 从历史+当前消息中提取搜索关键词 */
    private String buildSearchText(List<ConsultationMessage> history, String current) {
        StringBuilder sb = new StringBuilder();
        for (ConsultationMessage h : history) {
            if (h.getSenderType() == 1 && h.getContent() != null) sb.append(h.getContent()).append(" ");
        }
        sb.append(current);
        return sb.toString().trim();
    }

    /** RAG 向量检索（主）→ keyword 降级，返回 Medicine 列表 */
    private List<Medicine> searchDrugsVectorMeds(String query) {
        // 1. pgvector 语义向量检索
        try {
            float[] vec = embeddingService.embed(query);
            if (vec.length > 0) {
                List<Long> ids = vectorRepo.searchSimilar(vec, 5);
                if (!ids.isEmpty()) {
                    log.info("pgvector 命中 {}", ids.size());
                    return medicineRepository.findAllById(ids);
                }
            }
        } catch (Exception e) { log.warn("pgvector 检索失败: {}", e.getMessage()); }

        // 2. keyword 降级
        List<Medicine> kw = searchDrugsKeywordList(query);
        if (!kw.isEmpty()) return kw.stream().limit(5).toList();

        for (String sub : splitKeywords(query)) {
            kw = searchDrugsKeywordList(sub);
            if (!kw.isEmpty()) return kw.stream().limit(5).toList();
        }
        return List.of();
    }

    /** RAG 向量检索（主）→ keyword 降级，返回字符串（兼容旧调用） */
    private String searchDrugsVector(String query) {
        return formatMedicines(searchDrugsVectorMeds(query));
    }

    /** 短词拆分：2-4 字滑动窗口 */
    private List<String> splitKeywords(String query) {
        List<String> parts = new ArrayList<>();
        for (String s : query.split("[，。！？\\s,]+")) {
            if (s.length() >= 2 && s.length() <= 4) parts.add(s);
            else for (int i = 0; i <= s.length() - 2; i++)
                parts.add(s.substring(i, Math.min(i + 3, s.length())));
        }
        return parts;
    }

    private String searchDrugsKeyword(String kw) {
        List<Medicine> meds = searchDrugsKeywordList(kw);
        return meds.isEmpty() ? "" : formatMedicines(meds);
    }

    private List<Medicine> searchDrugsKeywordList(String kw) {
        try {
            return medicineRepository
                    .searchByKeyword(kw, org.springframework.data.domain.PageRequest.of(0, 5)).getContent();
        } catch (Exception e) { return List.of(); }
    }

    /** 将 Medicine 列表转为前端可渲染的 MedicineCard DTO */
    private List<AiReplyResponse.MedicineCard> toMedicineCards(List<Medicine> meds) {
        if (meds == null || meds.isEmpty()) return List.of();
        return meds.stream().map(m -> {
            AiReplyResponse.MedicineCard c = new AiReplyResponse.MedicineCard();
            c.setId(m.getId());
            c.setName(m.getName());
            c.setSpecification(m.getSpecification());
            c.setPrice(m.getPrice());
            c.setIsPrescription(m.getIsPrescription());
            c.setStock(m.getStock());
            c.setIndications(m.getIndications());
            return c;
        }).toList();
    }

    private String formatMedicines(List<Medicine> meds) {
        return meds.stream()
                .map(m -> String.format("- %s【%s】%s · ¥%.2f · %s · %s",
                        m.getName(), m.getIsPrescription() == 1 ? "RX" : "OTC",
                        m.getSpecification(), m.getPrice(),
                        m.getStock() > 0 ? "库存"+m.getStock() : "缺货",
                        m.getIndications() != null ? m.getIndications() : "暂无适应症"))
                .collect(Collectors.joining("\n"));
    }

    public TransferResponse transferToHuman(Long userId, Long sessionId, String reason) {
        Consultation s = consultationRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在"));
        if (!s.getUserId().equals(userId)) throw new BusinessException(403, "无权操作");
        s.setStatus(3); s.setIsTransferred(1); consultationRepository.save(s);
        TransferResponse r = new TransferResponse();
        r.setSessionId(sessionId); r.setStatus("waiting"); r.setQueuePosition(1);
        return r;
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
            ai.setRiskLevel(Math.min(Math.max(extractIntField(json, "riskLevel", 1), 1), 3));
        } else {
            ai.setContent(content); ai.setRiskLevel(1);
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

    // ========== 智能降级 ==========

    private AiReplyResponse.AiMsg fallbackWithContext(String searchText, String drugContext,
                                                       List<ConsultationMessage> history, String current) {
        AiReplyResponse.AiMsg ai = new AiReplyResponse.AiMsg();

        // 有药品匹配结果 → 直接推荐
        if (!drugContext.isEmpty()) {
            ai.setContent("根据您的描述，推荐以下药品：\n" + drugContext + "\n\n请在药师指导下选用。");
            ai.setMedicineAdvice(drugContext);
            ai.setRiskLevel(1);
            ai.setDisclaimer("本建议仅供参考。");
            return ai;
        }

        // 检查是否是追问（用户想知道更多药）
        String lower = searchText.toLowerCase();
        boolean isFollowUp = current.contains("药") || current.contains("什么") || current.contains("怎么")
                || current.contains("推荐") || current.contains("还有") || current.contains("其他");

        // 如果有症状关键词，给出具体建议
        if (lower.contains("头痛") || lower.contains("头晕") || lower.contains("发烧") || lower.contains("发热") || lower.contains("感冒") || lower.contains("咳嗽")) {
            ai.setContent("头痛/发热/咳嗽可能是感冒或流感引起。系统中可选用布洛芬缓释胶囊(¥19.90)或对乙酰氨基酚片(¥12.50)，请在药师指导下使用。");
            ai.setMedicineAdvice("布洛芬缓释胶囊 ¥19.90 / 对乙酰氨基酚片 ¥12.50");
            ai.setSymptomAnalysis("疑似上呼吸道感染症状");
        } else if (lower.contains("胃") || lower.contains("消化") || lower.contains("肚子") || lower.contains("腹泻") || lower.contains("拉肚子") || lower.contains("恶心") || lower.contains("呕吐")) {
            ai.setContent("消化系统不适。系统中可选用蒙脱石散(¥15.50)止泻，或奥美拉唑肠溶胶囊(¥28.00)缓解胃酸过多。注意清淡饮食，如症状持续请就医。");
            ai.setMedicineAdvice("蒙脱石散 ¥15.50 / 奥美拉唑肠溶胶囊 ¥28.00");
            ai.setSymptomAnalysis("疑似消化系统功能紊乱");
        } else if (lower.contains("过敏") || lower.contains("皮疹") || lower.contains("痒") || lower.contains("皮肤")) {
            ai.setContent("皮肤过敏症状。建议避免接触过敏原，可外用红霉素软膏(¥8.90)。如出现严重过敏反应（呼吸困难、面部肿胀）请立即就医。");
            ai.setMedicineAdvice("红霉素软膏 ¥8.90");
            ai.setSymptomAnalysis("疑似皮肤过敏反应");
        } else if (lower.contains("血压") || lower.contains("高压") || lower.contains("降压")) {
            ai.setContent("高血压需要长期规范治疗。氨氯地平贝那普利片(¥35.00)为处方药，需凭医师处方购买，请在医生指导下使用。");
            ai.setMedicineAdvice("氨氯地平贝那普利片 ¥35.00（处方药）");
            ai.setSymptomAnalysis("高血压相关咨询");
            ai.setRiskLevel(2);
        } else if (lower.contains("血糖") || lower.contains("糖尿") || lower.contains("胰岛素")) {
            ai.setContent("糖尿病用药需要严格医学监督。二甲双胍缓释片(¥28.80)为处方药，需凭医师处方购买。请勿自行调整用药方案。");
            ai.setMedicineAdvice("二甲双胍缓释片 ¥28.80（处方药）");
            ai.setSymptomAnalysis("糖尿病相关咨询");
            ai.setRiskLevel(3);
        } else if (isFollowUp) {
            ai.setContent("请告诉我您具体的症状（如头痛、胃痛、咳嗽、过敏等），我可以为您推荐系统中匹配的药品。");
        } else {
            ai.setContent("请详细描述您的症状（如头痛、发烧、咳嗽、胃痛、过敏等），告诉我哪里不舒服，以便为您推荐合适的药品。");
        }
        ai.setWarnings("如症状持续或加重，请及时就医。");
        ai.setRiskLevel(ai.getRiskLevel() != null ? ai.getRiskLevel() : 1);
        ai.setDisclaimer("本建议仅供参考，请遵医嘱或咨询专业药师。");
        return ai;
    }
}
