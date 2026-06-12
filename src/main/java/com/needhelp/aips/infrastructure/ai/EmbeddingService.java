package com.needhelp.aips.infrastructure.ai;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);
    private final EmbeddingModel model;

    public EmbeddingService(Environment env) {
        this.model = OpenAiEmbeddingModel.builder()
                .apiKey(env.getProperty("llm.api-key", "sk-your-api-key-here"))
                .baseUrl(env.getProperty("llm.base-url", "https://api.deepseek.com"))
                .modelName("deepseek-embedding")
                .timeout(Duration.ofSeconds(30))
                .build();
    }

    public float[] embed(String text) {
        try {
            Embedding emb = model.embed(text).content();
            float[] vec = new float[emb.vectorAsList().size()];
            for (int i = 0; i < vec.length; i++)
                vec[i] = emb.vectorAsList().get(i);
            return vec;
        } catch (Exception e) {
            log.error("Embedding 失败: {}", e.getMessage());
            return new float[0];
        }
    }
}
