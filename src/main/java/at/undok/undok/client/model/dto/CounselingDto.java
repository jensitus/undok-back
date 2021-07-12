package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CounselingDto {

    private UUID id;

    private String counselingStatus;

    private LocalDate entryDate;

    private String concern;

    private String concernCategory;

    private String activity;

    private String activityCategory;

    private String registeredBy;

    private LocalDateTime counselingDate;

    private LocalDateTime createdAt;

}
