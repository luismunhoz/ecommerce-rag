package com.ecommerce.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /** The AI-generated answer, grounded in the retrieved products. */
    private String answer;

    /** The products retrieved from pgvector that were used as context. */
    private List<ProductDTO> sources;
}
