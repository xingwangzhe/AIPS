package com.needhelp.aips.infrastructure.ai;

import com.needhelp.aips.common.config.OpenAiProperties;
import com.needhelp.aips.dto.consult.AiReplyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

/**
 * LLM API 客户端（兼容 DeepSeek / OpenAI 格式）。
 *
 * 模型区分：
 * - deepseek-v4-flash：快速模式，标准 chat completions
 * - deepseek-v4-pro ：推理模式，启用 thinking + reasoning_effort
 *
 * 未配置真实 API Key 时自动降级为关键词模拟回复。
 */
@Service
public class OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);

    private final RestClient restClient;
    private final OpenAiProperties props;

    public OpenAiClient(OpenAiProperties props) {
        this.props = props;
        this.restClient = RestClient.builder()
                .baseUrl(props.getApiUrl())
                .defaultHeader("Authorization", "Bearer " + props.getApiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 调用 LLM API 进行用药咨询。
     * 根据模型类型自动选择推理/快速模式。
     */
    @SuppressWarnings("unchecked")
    public AiReplyResponse.AiMsg chat(String userMessage) {
        if (props.getApiKey() == null || props.getApiKey().isBlank()
                || props.getApiKey().startsWith("sk-your-")) {
            log.warn("API Key 未配置或为占位值，使用模拟回复");
            return fallbackReply(userMessage);
        }

        try {
            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(userMessage);

            log.debug("调用 {} (推理模式={})", props.getModel(), props.isReasoningModel());

            Map<String, Object> response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            // 提取 choices[0].message.content
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");

            // 推理模式：reasoning_content 在单独字段中
            Object reasoning = message.get("reasoning_content");
            if (reasoning != null) {
                log.debug("推理链: {}", reasoning);
            }

            log.debug("LLM 回复: {}", content);
            return parseStructuredReply(content);

        } catch (Exception e) {
            log.error("LLM API 调用失败，降级为模拟回复: {}", e.getMessage());
            return fallbackReply(userMessage);
        }
    }

    /**
     * 根据不同模型构建不同的请求体。
     */
    private Map<String, Object> buildRequestBody(String userMessage) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", props.getModel());
        body.put("messages", List.of(
                Map.of("role", "system", "content", props.getSystemPrompt()),
                Map.of("role", "user", "content", userMessage)
        ));
        body.put("temperature", 0.7);
        body.put("max_tokens", 1024);

        // deepseek-v4-pro 推理模式：启用 thinking
        if (props.isReasoningModel()) {
            body.put("thinking", Map.of("type", "enabled"));
            body.put("reasoning_effort", props.getReasoningEffort());

            // 推理模型建议关闭 temperature（与 thinking 互斥）
            body.put("temperature", null); // 不传 temperature
        }

        return body;
    }

    // ==================== 响应解析（同前） ====================

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
            ai.setSymptomAnalysis("");
            ai.setMedicineAdvice("");
            ai.setWarnings("请参考上述回复内容，如症状持续请及时就医。");
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

    // ==================== 降级模拟 ====================

    private AiReplyResponse.AiMsg fallbackReply(String userContent) {
        AiReplyResponse.AiMsg ai = new AiReplyResponse.AiMsg();
        String lower = userContent.toLowerCase();

        if (lower.contains("头痛") || lower.contains("发烧") || lower.contains("感冒")) {
            ai.setContent("根据您的描述，头痛伴发烧可能是感冒引起的。建议服用解热镇痛类药物，并多休息多喝水。");
            ai.setSymptomAnalysis("头痛+发热，疑似感冒前驱症状");
            ai.setMedicineAdvice("可考虑服用对乙酰氨基酚片（500mg），每日不超过4次");
            ai.setWarnings("如症状持续超过3天或高烧超过38.5℃，请及时就医");
            ai.setRiskLevel(1);
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
            ai.setRiskLevel(2);
        } else if (lower.contains("糖尿病") || lower.contains("血糖") || lower.contains("胰岛素")) {
            ai.setContent("糖尿病用药需要严格的医学监督。AI不能替代内分泌科医生的诊断。");
            ai.setSymptomAnalysis("糖尿病相关咨询 - 高风险话题");
            ai.setMedicineAdvice("");
            ai.setWarnings("请务必在医生指导下调整降糖药或胰岛素用量，不可自行更改治疗方案");
            ai.setRiskLevel(3);
        } else {
            ai.setContent("感谢您的咨询。建议提供更多信息或转接人工药师获取更专业的帮助。");
            ai.setSymptomAnalysis("信息不足，无法进行症状分析");
            ai.setMedicineAdvice("");
            ai.setWarnings("建议详细描述症状，或前往医院就诊");
            ai.setRiskLevel(1);
        }
        ai.setDisclaimer("本建议仅供参考，请遵医嘱或咨询专业药师。如有严重不适，请立即就医。");
        return ai;
    }
}
