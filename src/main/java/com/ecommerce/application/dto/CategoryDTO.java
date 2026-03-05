package com.ecommerce.application.dto;

import com.ecommerce.domain.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;
    private String slug;
    private Long parentId;
    private List<CategoryDTO> children;

    public static CategoryDTO fromEntity(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .children(category.getChildren() != null
                        ? category.getChildren().stream().map(CategoryDTO::fromEntity).toList()
                        : null)
                .build();
    }
}
