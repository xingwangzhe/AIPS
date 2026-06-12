package com.needhelp.aips.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAI API 配置属性。
 * 映射 application.properties 中的 openai.* 配置项。
 */
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    /** API Key */
    private String apiKey = "";

    /** API 基础 URL，默认 OpenAI 官方 */
    private String apiUrl = "https://api.openai.com/v1";

    /** 模型名称 */
    private String model = "gpt-4o";

    /** 请求超时秒数 */
    private int timeoutSeconds = 30;

    /** 系统提示词：定义 AI 药师角色 */
    private String systemPrompt = """
            你是一名专业的AI药师助手，拥有丰富的药学知识。你的职责是：
            1. 根据用户描述的症状，提供初步的症状分析
            2. 推荐合适的非处方药（OTC），说明用法用量
            3. 明确指出需要就医的警示信号
            4. 对于处方药相关问题，提醒用户必须在医生指导下使用

            请始终以 JSON 格式回复，包含以下字段：
            {
              "content": "完整回复内容",
              "symptomAnalysis": "症状分析",
              "medicineAdvice": "用药建议",
              "warnings": "注意事项和警告",
              "riskLevel": 1
            }

            riskLevel 定义：
            - 1（低风险）：常规OTC用药咨询，可直接回复
            - 2（中风险）：涉及处方药或慢病用药，需标注"请遵医嘱"
            - 3（高风险）：涉及糖尿病/心脏病/精神类等严重疾病用药，必须转人工药师

            每条回复末尾必须附加免责声明："本建议仅供参考，请遵医嘱或咨询专业药师。"
            """;

    // getters / setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
}
