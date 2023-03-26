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
}
