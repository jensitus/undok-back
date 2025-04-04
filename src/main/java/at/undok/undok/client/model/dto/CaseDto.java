package at.undok.undok.client.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CaseDto {
    private UUID id;
    private String createdAt;
    private String updatedAt;
    private String name;
    private List<CounselingDto> counselings;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String referredTo;
    private UUID clientId;
    private Integer totalConsultationTime;

    public CaseDto(UUID id, String createdAt, String updatedAt, String name, String status,
                   LocalDate startDate, LocalDate endDate, String referredTo, UUID clientId, Integer totalConsultationTime) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.referredTo = referredTo;
        this.clientId = clientId;
        this.totalConsultationTime = totalConsultationTime;
    }
}
