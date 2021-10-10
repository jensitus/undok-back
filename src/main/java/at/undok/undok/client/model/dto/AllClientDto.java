package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AllClientDto {

    private UUID id;
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

    // person:

    private String type;
    // private String sex;
    private LocalDate dateOfBirth;
    private String lastName;
    private String firstName;

    // address:

    private String street;
    private String zipCode;
    private String city;
    private String country;

    private List<CounselingDto> counselings;

}
