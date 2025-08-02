package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@Table(name = "clients")
public class Client extends AbstractCrud {

    @Column(name = "keyword")
    @NotNull
    private String keyword;

    @Column(name = "education")
    private String education;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "language")
    private String language;

    @OneToOne(mappedBy = "client")
    @JsonIgnore
    private Person person;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "client_employer", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "employer_id"))
    private List<Employer> employers;

    @Column(name = "interpretation_necessary")
    private Boolean interpreterNecessary;

    @Column(name = "how_has_the_person_heard_from_us")
    private String howHasThePersonHeardFromUs;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy(value = "counselingDate asc")
    private List<Counseling> counselings;

    @Column(name = "vulnerable_when_asserting_rights")
    private Boolean vulnerableWhenAssertingRights;

    @Column(name = "current_resident_status")
    private String currentResidentStatus;

    @Column(name = "former_resident_status")
    private String formerResidentStatus;

    @Column(name = "labour_market_access")
    private String labourMarketAccess;

    @Column(name = "position")
    private String position;

    @Column(name = "sector")
    private String sector;

    @Column(name = "labour_union")
    private String union;

    @Column(name = "membership")
    private Boolean membership;

    @Column(name = "organization")
    private String organization;

    @Column(name = "status")
    private String status;

    @Column(name = "social_insurance_number")
    private String socialInsuranceNumber;

    @Column(name = "further_contact")
    private String furtherContact;

    @Column(name = "comment")
    private String comment;

}
