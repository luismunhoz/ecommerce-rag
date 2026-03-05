package com.ecommerce.infrastructure.adapter.in.rest;

import com.ecommerce.application.dto.CategoryDTO;
import com.ecommerce.application.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Hierarchical product categories — read operations are public; write operations require ADMIN")
public class CategoryController {

    private final CategoryService categoryService;

    // ------------------------------------------------------------------ public reads

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "List all categories")
    @ApiResponse(responseCode = "200", description = "Full list of categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/root")
    @SecurityRequirements
    @Operation(summary = "List root categories", description = "Returns only top-level categories (those with no parent).")
    @ApiResponse(responseCode = "200", description = "List of root categories")
    public ResponseEntity<List<CategoryDTO>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(summary = "Get category by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryDTO> getCategory(
            @Parameter(description = "Category ID") @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @GetMapping("/slug/{slug}")
    @SecurityRequirements
    @Operation(summary = "Get category by slug", description = "Slug is auto-generated from the category name on creation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryDTO> getCategoryBySlug(
            @Parameter(description = "URL-friendly slug, e.g. `electronics`") @PathVariable String slug) {
        return ResponseEntity.ok(categoryService.getCategoryBySlug(slug));
    }

    // ------------------------------------------------------------------ admin writes

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create category [ADMIN]")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    public ResponseEntity<CategoryDTO> createCategory(
            @Parameter(description = "Category name", required = true) @RequestParam String name,
            @Parameter(description = "Optional description") @RequestParam(required = false) String description,
            @Parameter(description = "ID of the parent category (omit for root)") @RequestParam(required = false) Long parentId) {
        CategoryDTO category = categoryService.createCategory(name, description, parentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update category [ADMIN]", description = "Only the supplied fields are updated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Parameter(description = "New name") @RequestParam(required = false) String name,
            @Parameter(description = "New description") @RequestParam(required = false) String description) {
        return ResponseEntity.ok(categoryService.updateCategory(id, name, description));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete category [ADMIN]")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
