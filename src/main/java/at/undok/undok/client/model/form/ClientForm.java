package at.undok.undok.client.model.form;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@ToString
public class ClientForm {
    private String firstName;
    private String lastName;
    private String dateOfBirth;

    private String street;
    private String zipCode;
    private String city;
    private String country;

    private String keyword;

    private String education;

    private String maritalStatus;

    private Boolean interpreterNecessary;

    private String howHasThePersonHeardFromUs;

    private Boolean vulnerableWhenAssertingRights;

    // private Nationality nationality;

    // private Set<Language> language;

    // private ResidentStatus currentResidentStatus;

    // private ResidentStatus formerResidentStatus;

    // private LabourMarketAccess labourMarketAccess;

    // private String position;

    // private Industry industry;

    // private Union union;

    // private Boolean membership;

    // private String organisation;
    
}
