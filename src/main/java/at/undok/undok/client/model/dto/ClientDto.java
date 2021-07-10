package at.undok.undok.client.model.dto;

import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.enumeration.MaritalStatus;
import at.undok.undok.client.model.enumeration.ResidentStatus;
import lombok.Data;

import javax.persistence.OneToOne;
import java.util.List;
import java.util.UUID;

@Data
public class ClientDto {

    private UUID id;

    private String keyword;

    private String education;

    private String maritalStatus;

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

    private PersonDto personDto;

    private List<CounselingDto> counselings;

}
