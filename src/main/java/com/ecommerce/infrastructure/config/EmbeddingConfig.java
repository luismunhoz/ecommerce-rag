package com.ecommerce.infrastructure.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class EmbeddingConfig {

    @Value("${OPENAI_API_KEY}")
    private String openAiApiKey;

    @Value("${app.rag.chat-model:gpt-4o-mini}")
    private String chatModelName;

    @Value("${app.rag.temperature:0.3}")
    private double temperature;

    @Value("${DB_HOST:localhost}")
    private String dbHost;

    @Value("${DB_PORT:5432}")
    private int dbPort;

    @Value("${DB_NAME:ecommerce}")
    private String dbName;

    @Value("${DB_USERNAME:ecommerce}")
    private String dbUsername;

    @Value("${DB_PASSWORD:ecommerce}")
    private String dbPassword;

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(openAiApiKey)
                .modelName("text-embedding-3-small") // 1536 dimensions, low cost
                .build();
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(chatModelName)
                .temperature(temperature)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(dbHost)
                .port(dbPort)
                .database(dbName)
                .user(dbUsername)
                .password(dbPassword)
                .table("product_embeddings")
                .dimension(1536)
                .createTable(false) // table is managed by Flyway (V7 migration)
                .build();
    }
}
