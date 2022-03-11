package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CategoryDto {
    private UUID id;
    private String name;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
