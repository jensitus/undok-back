package at.undok.undok.client.model.entity;

import at.undok.undok.client.model.enumeration.LabourMarketAccess;
import at.undok.undok.client.model.enumeration.MaritalStatus;
import at.undok.undok.client.model.enumeration.ResidentStatus;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "clients")
public class Client extends Person {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    private String keyword;

    private String education;

    private MaritalStatus maritalStatus;

    @OneToOne
    private Nationality nationality;

    @OneToMany
    private Set<Language> language;

    private Boolean interpreterNecessary;

    private String howHasThePersonHeardFromUs;

    private ResidentStatus currentResidentStatus;

    private Boolean vulnerableWhenAssertingRights;

    private ResidentStatus formerResidentStatus;

    private LabourMarketAccess labourMarketAccess;

    private String position;

    @OneToOne
    private Industry industry;

    @OneToOne
    private Union union;

    private Boolean membership;

    private String organisation;

}
