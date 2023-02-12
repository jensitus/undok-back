package at.undok.undok.client.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class JoinCategoryDto {
    private UUID id;
    private UUID categoryId;
    private UUID entityId;
    private String categoryType;
    private String entityType;
}
