package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AllCounselingDto {
    private UUID id;
    private String counselingStatus;
    private LocalDate entryDate;
    private String concern;
    private String concernCategory;
    private String activity;
    private String activityCategories;
    private String registeredBy;
    private LocalDateTime counselingDate;
    private LocalDateTime createdAt;
    private String comment;
    private UUID clientId;
    private String clientFullName;
    private String keyword;
    private Integer requiredTime;
}
