package at.undok.undok.client.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String referredTo;

    public CaseDto(UUID id, String createdAt, String updatedAt, String name, String status,
                   LocalDateTime startTime,
                   LocalDateTime endTime, String referredTo) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.referredTo = referredTo;
    }
}
