package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.dto.CounselingResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "counselings")
@NamedNativeQuery(name = "getCounselingDto",
        query = "select co.id as id, co.client_id as clientId, co.activity as activity, " +
                "co.activity_category as activityCategory, co.comment as comment," +
                "co.counseling_date as counselingDate, co.concern as concern, " +
                "co.concern_category as concernCategory, cl.keyword as keyword " +
                "from counselings co, clients cl where co.client_id = cl.id and cl.status = 'ACTIVE'",
        resultSetMapping = "setToCounselingDto")
@SqlResultSetMapping(name = "setToCounselingDto", classes = @ConstructorResult(targetClass = CounselingResult.class, columns = {
        @ColumnResult(name = "id", type = UUID.class),
        @ColumnResult(name = "concern", type = String.class),
        @ColumnResult(name = "concernCategory", type = String.class),
        @ColumnResult(name = "activity", type = String.class),
        @ColumnResult(name = "activityCategory", type = String.class),
        @ColumnResult(name = "counselingDate", type = LocalDateTime.class),
        @ColumnResult(name = "comment", type = String.class),
        @ColumnResult(name = "clientId", type = UUID.class),
        @ColumnResult(name = "keyword", type = String.class)
}))
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

    @Column(name = "status")
    private String status;

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
