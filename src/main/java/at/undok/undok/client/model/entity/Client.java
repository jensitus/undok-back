package at.undok.undok.client.model.entity;

import at.undok.undok.client.model.enumeration.MaritalStatus;
import at.undok.undok.client.model.enumeration.ResidentStatus;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", unique = true, nullable = false, insertable = false, updatable = false)
    private UUID id;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "education")
    private String education;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "language")
    private String language;

    @OneToOne(mappedBy = "client", fetch = FetchType.EAGER)
    private Person person;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "client_employer", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "employer_id"))
    private List<Employer> employers;

    @Column(name = "interpretation_necessary")
    private Boolean interpreterNecessary;

    @Column(name = "how_has_the_person_heard_from_us")
    private String howHasThePersonHeardFromUs;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
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


    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", keyword='" + keyword + '\'' +
                ", education='" + education + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", interpreterNecessary=" + interpreterNecessary +
                ", howHasThePersonHeardFromUs='" + howHasThePersonHeardFromUs + '\'' +
                ", vulnerableWhenAssertingRights=" + vulnerableWhenAssertingRights +
                '}';
    }
}
