package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskDto {

    private UUID id;
    private String title;
    private String description;
    private String dueDate;
    private Integer requiredTime;
    private String status;
    private UUID caseId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID clientId;

}
