package at.undok.undok.client.model.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
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
    private List<CaseDto> closedCases;
    private CaseDto openCase;
    private List<CategoryDto> jobFunctions;

    private String furtherContact;
    private String comment;

    private Boolean alert;

    public ClientDto(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt, String keyword, String education,
                     String maritalStatus, String nationality, String language, Boolean interpreterNecessary,
                     String howHasThePersonHeardFromUs, String currentResidentStatus, Boolean vulnerableWhenAssertingRights,
                     String formerResidentStatus, String labourMarketAccess, String position, String sector, String union,
                     Boolean membership, String organization, String socialInsuranceNumber, String furtherContact,
                     String comment, Boolean alert) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.keyword = keyword;
        this.education = education;
        this.maritalStatus = maritalStatus;
        this.nationality = nationality;
        this.language = language;
        this.interpreterNecessary = interpreterNecessary;
        this.howHasThePersonHeardFromUs = howHasThePersonHeardFromUs;
        this.currentResidentStatus = currentResidentStatus;
        this.vulnerableWhenAssertingRights = vulnerableWhenAssertingRights;
        this.formerResidentStatus = formerResidentStatus;
        this.labourMarketAccess = labourMarketAccess;
        this.position = position;
        this.sector = sector;
        this.union = union;
        this.membership = membership;
        this.organization = organization;
        this.socialInsuranceNumber = socialInsuranceNumber;
        this.furtherContact = furtherContact;
        this.comment = comment;
        this.alert = alert;
    }
}
