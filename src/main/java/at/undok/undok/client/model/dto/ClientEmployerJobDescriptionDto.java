package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ClientEmployerJobDescriptionDto {
    private UUID id;
    private EmployerDto employer;
    private LocalDate from;
    private LocalDate until;
    private String industry;
    private String industrySub;
    private String jobFunction;
    private String jobRemarks;
}
