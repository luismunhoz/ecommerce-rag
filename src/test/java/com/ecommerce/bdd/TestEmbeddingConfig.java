package com.ecommerce.bdd;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Profile("test")
public class TestEmbeddingConfig {

    @Bean
    public EmbeddingModel embeddingModel() {
        return textSegments -> {
            List<Embedding> embeddings = textSegments.stream()
                    .map(s -> Embedding.from(new float[1536]))
                    .collect(Collectors.toList());
            return Response.from(embeddings);
        };
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return messages -> Response.from(AiMessage.from("stub response"));
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new EmbeddingStore<>() {
            @Override
            public String add(Embedding embedding) { return "stub-id"; }

            @Override
            public void add(String id, Embedding embedding) {}

            @Override
            public String add(Embedding embedding, TextSegment textSegment) { return "stub-id"; }

            @Override
            public List<String> addAll(List<Embedding> embeddings) { return Collections.emptyList(); }

            @Override
            public List<String> addAll(List<Embedding> embeddings, List<TextSegment> textSegments) {
                return Collections.emptyList();
            }
        };
    }
}
