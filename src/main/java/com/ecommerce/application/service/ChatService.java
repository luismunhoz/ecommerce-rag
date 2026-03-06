package com.ecommerce.application.service;

import com.ecommerce.application.dto.ChatResponse;
import com.ecommerce.application.dto.ProductDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RAG orchestrator.
 *
 * <p>Pipeline:
 * <ol>
 *   <li><b>Retrieve</b>  — find the most relevant products via pgvector cosine similarity.</li>
 *   <li><b>Augment</b>   — inject those products as structured context into a prompt.</li>
 *   <li><b>Generate</b>  — send the prompt to the LLM and return its answer.</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ProductEmbeddingService embeddingService;
    private final ChatLanguageModel chatLanguageModel;

    @Value("${app.rag.max-context-products:5}")
    private int maxContextProducts;

    public ChatResponse ask(String question) {
        log.info("RAG query: '{}'", question);

        // ── 1. RETRIEVE ──────────────────────────────────────────────────────
        List<ProductDTO> sources = embeddingService.search(question, maxContextProducts);
        log.debug("Retrieved {} products as context", sources.size());

        // ── 2. AUGMENT ───────────────────────────────────────────────────────
        String prompt = buildPrompt(question, sources);

        // ── 3. GENERATE ──────────────────────────────────────────────────────
        String answer = chatLanguageModel.generate(prompt);
        log.info("RAG answer generated ({} chars)", answer.length());

        return ChatResponse.builder()
                .answer(answer)
                .sources(sources)
                .build();
    }

    // ── prompt builder ────────────────────────────────────────────────────────

    private String buildPrompt(String question, List<ProductDTO> products) {
        if (products.isEmpty()) {
            return """
                    You are a helpful assistant for a computer equipment store.
                    A customer asked: "%s"
                    No relevant products were found in our catalog for this query.
                    Politely let the customer know and suggest they refine their search.
                    """.formatted(question);
        }

        StringBuilder context = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            ProductDTO p = products.get(i);
            context.append("""
                    Product %d:
                      Name: %s
                      Price: $%.2f
                      Category: %s
                      In Stock: %s
                      Description: %s

                    """.formatted(
                    i + 1,
                    p.getName(),
                    p.getPrice(),
                    p.getCategoryName() != null ? p.getCategoryName() : "N/A",
                    p.isInStock() ? "Yes (" + p.getStockQuantity() + " units)" : "No",
                    p.getDescription() != null ? p.getDescription() : "N/A"
            ));
        }

        return """
                You are a knowledgeable and friendly assistant for a computer equipment store.
                Use ONLY the products listed below to answer the customer's question.
                If the listed products do not fully match what the customer needs, be honest about it.
                Always mention product names and prices when making recommendations.
                Keep your answer concise, helpful, and to the point.

                --- AVAILABLE PRODUCTS ---
                %s
                --- END OF PRODUCTS ---

                Customer question: %s

                Your answer:
                """.formatted(context.toString(), question);
    }
}
