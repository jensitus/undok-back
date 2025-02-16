package at.undok.undok.client.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CounselingDto {

    private UUID id;
    private String counselingStatus;
    private LocalDate entryDate;
    private String concern;
    private String activity;
    private String registeredBy;
    private String counselingDate;
    private LocalDateTime createdAt;
    private String comment;
    private UUID clientId;
    private String clientFullName;
    private String keyword;
    private Integer requiredTime;
    private CaseDto counselingCase;

    public CounselingDto(UUID id, String concern, String activity, String counselingDate, String comment, UUID clientId, String keyword) {
        this.id = id;
        this.concern = concern;
        this.activity = activity;
        this.counselingDate = counselingDate;
        this.comment = comment;
        this.clientId = clientId;
        this.keyword = keyword;
    }

    public CounselingDto(UUID id, String concern, String activity, String registeredBy, String counselingDate,
                         LocalDateTime createdAt, String comment, UUID clientId, String clientFullName,
                         String keyword, Integer requiredTime) {
        this.id = id;
        this.concern = concern;
        this.activity = activity;
        this.registeredBy = registeredBy;
        this.counselingDate = counselingDate;
        this.createdAt = createdAt;
        this.comment = comment;
        this.clientId = clientId;
        this.clientFullName = clientFullName;
        this.keyword = keyword;
        this.requiredTime = requiredTime;
    }
}
