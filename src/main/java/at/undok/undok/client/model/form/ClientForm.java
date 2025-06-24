package at.undok.undok.client.model.form;

import lombok.Data;
import lombok.ToString;

import java.util.List;

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
    private String socialInsuranceNumber;

    private String targetGroup;
    private Boolean humanTrafficking;
    private Boolean jobCenterBlock;
    private String workingRelationship;

    private List<JoinCategoryForm> jobMarketAccessSelected;
    private List<JoinCategoryForm> jobMarketAccessDeSelected;
    private List<JoinCategoryForm> counselingLanguageSelected;
    private List<JoinCategoryForm> counselingLanguageDeSelected;
    private List<JoinCategoryForm> originOfAttentionSelected;
    private List<JoinCategoryForm> undocumentedWorkSelected;
    private List<JoinCategoryForm> complaintsSelected;

}
