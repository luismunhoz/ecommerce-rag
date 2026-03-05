package com.ecommerce.application.service;

import com.ecommerce.application.dto.ProductDTO;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.out.ProductRepository;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ProductRepository productRepository;

    @Value("${app.semantic-search.max-results:10}")
    private int defaultMaxResults;

    @Value("${app.semantic-search.min-score:0.65}")
    private double minScore;

    /**
     * Creates or replaces the embedding for a product.
     * Called after every create and update operation.
     *
     * The product ID is encoded into the UUID as new UUID(0L, productId), making it
     * fully reversible without metadata: UUID.getLeastSignificantBits() → productId.
     */
    public void indexProduct(Product product) {
        try {
            String text = buildEmbeddingText(product);
            Embedding embedding = embeddingModel.embed(text).content();
            String embeddingId = toEmbeddingId(product.getId());

            // Replace any existing embedding for this product
            try {
                embeddingStore.remove(embeddingId);
            } catch (UnsupportedOperationException ignored) {
                // PgVectorEmbeddingStore supports remove(), but guard just in case
            }
            embeddingStore.add(embeddingId, embedding);

            log.debug("Indexed product id={} sku={}", product.getId(), product.getSku());
        } catch (Exception e) {
            log.error("Failed to index product id={}: {}", product.getId(), e.getMessage());
        }
    }

    /**
     * Removes the embedding for a deleted product.
     */
    public void removeProduct(Long productId) {
        try {
            embeddingStore.remove(toEmbeddingId(productId));
            log.debug("Removed embedding for product id={}", productId);
        } catch (Exception e) {
            log.error("Failed to remove embedding for product id={}: {}", productId, e.getMessage());
        }
    }

    /**
     * Finds products semantically similar to the given query.
     * Only active products that exist in the database are returned,
     * ordered by relevance score descending.
     *
     * @param query      natural language query, e.g. "I need products for my home office"
     * @param maxResults maximum number of products to return
     */
    public List<ProductDTO> search(String query, int maxResults) {
        try {
            Embedding queryEmbedding = embeddingModel.embed(query).content();
            List<EmbeddingMatch<TextSegment>> matches =
                    embeddingStore.findRelevant(queryEmbedding, maxResults, minScore);

            if (matches.isEmpty()) {
                return List.of();
            }

            // Decode productId from the embedding UUID, preserving relevance order
            // and deduplicating (in case of stale duplicate embeddings)
            Map<Long, Integer> rankByProductId = new LinkedHashMap<>();
            int rank = 0;
            for (EmbeddingMatch<TextSegment> match : matches) {
                Long productId = toProductId(match.embeddingId());
                if (!rankByProductId.containsKey(productId)) {
                    rankByProductId.put(productId, rank++);
                }
            }

            List<Long> productIds = new ArrayList<>(rankByProductId.keySet());

            return productRepository.findAllById(productIds).stream()
                    .filter(Product::isActive)
                    .sorted(Comparator.comparingInt(p -> rankByProductId.getOrDefault(p.getId(), Integer.MAX_VALUE)))
                    .map(ProductDTO::fromEntity)
                    .toList();

        } catch (Exception e) {
            log.error("Semantic search failed for query='{}': {}", query, e.getMessage());
            return List.of();
        }
    }

    /**
     * Re-indexes every product in the database.
     * Use this after a bulk data import or first startup with existing products.
     */
    public void reindexAll() {
        log.info("Starting full product re-index...");
        List<Product> products = productRepository.findAll();
        int success = 0;
        for (Product product : products) {
            indexProduct(product);
            success++;
        }
        log.info("Re-index complete: {}/{} products indexed", success, products.size());
    }

    // --- helpers ---

    private String buildEmbeddingText(Product product) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(product.getName());
        if (product.getCategory() != null) {
            sb.append("\nCategory: ").append(product.getCategory().getName());
        }
        if (product.getDescription() != null && !product.getDescription().isBlank()) {
            sb.append("\nDescription: ").append(product.getDescription());
        }
        return sb.toString();
    }

    /**
     * Encodes a product ID into a stable UUID.
     * Format: 00000000-0000-0000-0000-{productId as 12-digit hex}
     * Fully reversible via {@link #toProductId(String)}.
     */
    private String toEmbeddingId(Long productId) {
        return new UUID(0L, productId).toString();
    }

    /** Recovers the product ID from an embedding UUID created by {@link #toEmbeddingId(Long)}. */
    private Long toProductId(String embeddingId) {
        return UUID.fromString(embeddingId).getLeastSignificantBits();
    }
}
