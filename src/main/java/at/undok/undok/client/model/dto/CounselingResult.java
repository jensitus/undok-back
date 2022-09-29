package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CounselingResult {
    private UUID id;

    private String concern;
    private String concernCategory;
    private String activity;
    private String activityCategory;
    private LocalDateTime counselingDate;
    private String comment;
    private UUID clientId;
    // private String clientFullName;
    private String keyword;

    public CounselingResult() {
    }

    public CounselingResult(UUID id, String concern, String concernCategory, String activity, String activityCategory, LocalDateTime counselingDate, String comment, UUID clientId, String keyword) {
        this.id = id;
        this.concern = concern;
        this.concernCategory = concernCategory;
        this.activity = activity;
        this.activityCategory = activityCategory;
        this.counselingDate = counselingDate;
        this.comment = comment;
        this.clientId = clientId;
        this.keyword = keyword;
    }
}
