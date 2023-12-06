package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ClientDto {

    private UUID id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String keyword;

    private String education;

    private String maritalStatus;

    private String nationality;

    private String language;

    private Boolean interpreterNecessary;

    private String howHasThePersonHeardFromUs;

    private String currentResidentStatus;

    private Boolean vulnerableWhenAssertingRights;

    private String formerResidentStatus;

    private String labourMarketAccess;

    private String position;

    private String sector;

    private String union;

    private Boolean membership;

    private String organization;

    private PersonDto person;

    private List<CounselingDto> counselings;

    private String socialInsuranceNumber;

}
