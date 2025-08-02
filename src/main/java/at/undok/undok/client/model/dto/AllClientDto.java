package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AllClientDto {

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
    private String socialInsuranceNumber;

    // person:

    private String type;
    private LocalDate dateOfBirth;
    private String lastName;
    private String firstName;
    private String email;
    private String telephone;
    private String gender;

    // address:

    private String street;
    private String zipCode;
    private String city;
    private String country;

    private List<CounselingDto> counselings;
    private String furtherContact;
    private String comment;
}
