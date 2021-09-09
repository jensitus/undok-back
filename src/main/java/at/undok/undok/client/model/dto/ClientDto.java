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

}
