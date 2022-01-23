package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientEmployerJobDescriptionDto {
     private EmployerDto employer;
     private LocalDate from;
     private LocalDate until;
     private String industry;
     private String industrySub;
     private String jobFunction;
     private String jobRemarks;
}
