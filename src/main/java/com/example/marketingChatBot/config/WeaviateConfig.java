package com.example.marketingChatBot.config;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeaviateConfig {

    @Value("${OPENAI_API_KEY}")
    private String openAiApiKey;

    @Bean
    public EmbeddingModel embeddingModel() {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(openAiApiKey)
                .build();

        return new OpenAiEmbeddingModel(openAiApi);
    }

    @Bean
    public WeaviateClient weaviateClient() {
        return new WeaviateClient(new Config("http", "localhost:8081"));
    }
}
