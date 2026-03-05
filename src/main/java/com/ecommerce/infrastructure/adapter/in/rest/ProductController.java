package com.ecommerce.infrastructure.adapter.in.rest;

import com.ecommerce.application.dto.CreateProductRequest;
import com.ecommerce.application.dto.ProductDTO;
import com.ecommerce.application.dto.UpdateProductRequest;
import com.ecommerce.application.service.ProductEmbeddingService;
import com.ecommerce.application.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog — read operations are public; write operations require ADMIN")
public class ProductController {

    private final ProductService productService;
    private final ProductEmbeddingService productEmbeddingService;

    // ------------------------------------------------------------------ public reads

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "List all products", description = "Returns a paginated list of all products, sorted by creation date descending by default.")
    @ApiResponse(responseCode = "200", description = "Page of products")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(summary = "Get product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductDTO> getProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping("/active")
    @SecurityRequirements
    @Operation(summary = "List active products", description = "Returns all products where active = true.")
    @ApiResponse(responseCode = "200", description = "List of active products")
    public ResponseEntity<List<ProductDTO>> getActiveProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    @GetMapping("/category/{categoryId}")
    @SecurityRequirements
    @Operation(summary = "List products by category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of products in the given category"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @Parameter(description = "Category ID") @PathVariable Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/search")
    @SecurityRequirements
    @Operation(summary = "Keyword search", description = "Case-insensitive search on product name. Returns a paginated result.")
    @ApiResponse(responseCode = "200", description = "Page of matching products")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @Parameter(description = "Search term", example = "keyboard") @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(q, pageable));
    }

    @GetMapping("/semantic-search")
    @SecurityRequirements
    @Operation(
            summary = "Semantic / AI-powered search",
            description = """
                    Finds products semantically relevant to a natural language query using OpenAI embeddings \
                    and pgvector cosine similarity. Only active products that exist in the database are returned, \
                    ordered by relevance score.

                    **Example queries:**
                    - `I need products for my home office`
                    - `something to keep my drinks cold`
                    - `gifts for a gamer`

                    Results with a similarity score below **0.65** are filtered out automatically.
                    """)
    @ApiResponse(responseCode = "200", description = "List of semantically relevant products ordered by relevance")
    public ResponseEntity<List<ProductDTO>> semanticSearch(
            @Parameter(description = "Natural language search query", example = "I need products for my home office")
            @RequestParam String q,
            @Parameter(description = "Maximum number of results to return (default 10)")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productEmbeddingService.search(q, limit));
    }

    // ------------------------------------------------------------------ admin writes

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create product [ADMIN]", description = "Creates a new product and indexes it for semantic search.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDTO product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product [ADMIN]", description = "Updates an existing product. Only non-null fields are changed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product [ADMIN]", description = "Deletes a product and removes it from the semantic search index.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reindex")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Re-index all products [ADMIN]",
            description = """
                    Re-generates OpenAI embeddings for every product in the database and stores them in pgvector. \
                    Use this after a bulk import or if the semantic search index becomes stale. \
                    This operation calls the OpenAI API once per product and may take a few seconds for large catalogs.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Re-index completed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    public ResponseEntity<Void> reindexProducts() {
        productEmbeddingService.reindexAll();
        return ResponseEntity.ok().build();
    }
}
