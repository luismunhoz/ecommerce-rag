package com.ecommerce.infrastructure.adapter.in.rest;

import com.ecommerce.application.dto.ChatRequest;
import com.ecommerce.application.dto.ChatResponse;
import com.ecommerce.application.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat (RAG)", description = "Full RAG pipeline: retrieves relevant products from pgvector, then generates an AI answer using those products as context.")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/ask")
    @SecurityRequirements
    @Operation(
            summary = "Ask the AI assistant",
            description = """
                    Implements the full Retrieval-Augmented Generation (RAG) pipeline:

                    1. **Retrieve** — embeds the question with OpenAI and finds the most \
                    similar products in pgvector (cosine similarity).
                    2. **Augment**  — injects those products as structured context into a prompt.
                    3. **Generate** — sends the prompt to GPT-4o-mini and returns its answer.

                    The response includes both the AI-generated answer and the source products \
                    that were used as context, so the UI can display transparent citations.

                    **Example questions:**
                    - "What laptop should I buy for video editing under $2000?"
                    - "I need a complete home office setup with a sit-stand desk"
                    - "What is the best wireless gaming mouse you have?"
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI answer with source products"),
            @ApiResponse(responseCode = "400", description = "Question is blank or too long")
    })
    public ResponseEntity<ChatResponse> ask(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.ask(request.getQuestion()));
    }
}
