package at.undok.undok.client.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class JoinCategoryDto {
    private Long id;
    private UUID categoryId;
    private UUID entityId;
    private String categoryType;
    private String entityType;

    public JoinCategoryDto(UUID categoryId, UUID entityId, String categoryType, String entityType) {
        this.categoryId = categoryId;
        this.entityId = entityId;
        this.categoryType = categoryType;
        this.entityType = entityType;
    }
}
