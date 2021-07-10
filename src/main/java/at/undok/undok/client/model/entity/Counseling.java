package at.undok.undok.client.model.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "counselings")
public class Counseling {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "counseling_status")
    private String counselingStatus;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "concern")
    private String concern;

    @Column(name = "concern_category")
    private String concernCategory;

    @Column(name = "activity")
    private String activity;

    @Column(name = "activity_category")
    private String activityCategory;

    @Column(name = "registered_by")
    private String registeredBy;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @Override
    public String toString() {
        return "Counseling{" +
                "id=" + id +
                ", counselingStatus='" + counselingStatus + '\'' +
                ", entryDate=" + entryDate +
                ", concern='" + concern + '\'' +
                ", concernCategory='" + concernCategory + '\'' +
                ", activity='" + activity + '\'' +
                ", activityCategory='" + activityCategory + '\'' +
                ", registeredBy='" + registeredBy + '\'' +
                '}';
    }
}
