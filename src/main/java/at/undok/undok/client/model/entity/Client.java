package at.undok.undok.client.model.entity;

import at.undok.undok.client.model.enumeration.MaritalStatus;
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

    // @OneToOne
    // private Nationality nationality;

    // @OneToMany
    // private Set<Language> language;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="person_id", referencedColumnName = "id")
    private Person person;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "employer_id", referencedColumnName = "id")
    private List<Employer> employers;

    @Column(name = "interpretation_necessary")
    private Boolean interpreterNecessary;

    @Column(name = "how_has_the_person_heard_from_us")
    private String howHasThePersonHeardFromUs;

    @OneToMany(mappedBy = "client")
    private List<Counseling> counselings;

    // private ResidentStatus currentResidentStatus;

    @Column(name = "vulnerable_when_asserting_rights")
    private Boolean vulnerableWhenAssertingRights;

    // private ResidentStatus formerResidentStatus;

    // private LabourMarketAccess labourMarketAccess;

    // private String position;

    // @OneToOne
    // private Industry industry;

    // @OneToOne
    // private Union union;

    // private Boolean membership;

    // private String organisation;


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
