package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "counselings")
public class Counseling extends AbstractCrud {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "counseling_date")
    private LocalDateTime counselingDate;

    @Column(name = "comment")
    private String comment;

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
