package at.undok.undok.client.model.form;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientForm {
    // Person
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String contactData;
    private String email;
    private String telephone;
    private String gender;

    // Address
    private String street;
    private String zipCode;
    private String city;
    private String country;

    // Employer:


    // Client:
    private String keyword;
    private String education;
    private String maritalStatus;
    private Boolean interpreterNecessary;
    private String howHasThePersonHeardFromUs;
    private Boolean vulnerableWhenAssertingRights;
    private String nationality;
    private String language;
    private String currentResidentStatus;
    private String formerResidentStatus;
    private String labourMarketAccess;
    private String position;
    private String sector;
    private String union;
    private Boolean membership;
    private String organization;

}
