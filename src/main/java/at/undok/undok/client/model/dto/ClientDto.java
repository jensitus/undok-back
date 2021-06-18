package at.undok.undok.client.model.dto;

import at.undok.undok.client.model.enumeration.MaritalStatus;
import at.undok.undok.client.model.enumeration.ResidentStatus;
import lombok.Data;

import javax.persistence.OneToOne;
import java.util.UUID;

@Data
public class ClientDto extends PersonDto {

    private UUID id;

    private String keyword;

    private String education;

    private MaritalStatus maritalStatus;

    @OneToOne
    private NationalityDto nationality;

    private String language;

    private Boolean interpreterNecessary;

    private String howHasThePersonHeardFromUs;

    private ResidentStatus currentResidentStatus;

    private Boolean vulnerableWhenAssertingRights;

    private ResidentStatus formerResidentStatus;

    private String labourMarketAccess;

    private String position;

    private IndustryDto industry;

    private String union;

    private Boolean membership;

    private String organisation;

}
