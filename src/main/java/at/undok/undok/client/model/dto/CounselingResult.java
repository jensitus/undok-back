package at.undok.undok.client.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CounselingResult {
    private UUID id;
    private String activity;
    private String comment;
    private LocalDateTime counselingDate;
    private String concern;
    private String registeredBy;
    private String keyword;
    private String legalCategories;
    private String activityCategories;

}
