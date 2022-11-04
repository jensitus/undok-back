package at.undok.undok.client.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CounselingDto {

    private UUID id;
    private String counselingStatus;
    private LocalDate entryDate;
    private String concern;
    private String concernCategory;
    private String activity;
    private Set<CategoryDto> activityCategories;
    private String registeredBy;
    private LocalDateTime counselingDate;
    private LocalDateTime createdAt;
    private String comment;
    private UUID clientId;
    private String clientFullName;
    private String keyword;

    public CounselingDto(UUID id, String concern, String concernCategory, String activity, LocalDateTime counselingDate, String comment, UUID clientId, String keyword) {
        this.id = id;
        this.concern = concern;
        this.concernCategory = concernCategory;
        this.activity = activity;
        this.counselingDate = counselingDate;
        this.comment = comment;
        this.clientId = clientId;
        this.keyword = keyword;
    }
}
