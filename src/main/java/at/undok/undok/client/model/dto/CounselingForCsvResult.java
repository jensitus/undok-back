package at.undok.undok.client.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CounselingForCsvResult {
    private UUID id;
    private String activity;
    private String comment;
    private LocalDateTime counselingDate;
    private String concern;
    private String registeredBy;
    private String keyword;
    private String clientId;
    private String legalCategories;
    private String activityCategories;

}
