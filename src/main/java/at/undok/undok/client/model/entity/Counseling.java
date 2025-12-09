package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import at.undok.undok.client.model.dto.CounselingForCsvResult;
import io.hypersistence.utils.hibernate.type.search.PostgreSQLTSVectorType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Entity
@Table(name = "counselings")
@NamedNativeQuery(name = "getCounselingsForCsv",
        query = "select co.id as id, " +
                "co.activity as activity, " +
                "co.comment as comment, " +
                "co.counseling_date as counselingDate, " +
                "co.concern as concern, " +
                "co.registered_by as registeredBy, " +
                "cl.keyword as keyword, " +
                "co.client_id as clientId, " +
                "(select string_agg(ca.name, ',') from categories ca, join_category jc " +
                "where jc.entity_id = co.id and jc.category_id = ca.id and jc.category_type = 'LEGAL') as legalCategories, " +
                "(select string_agg(ca.name, ',') from categories ca, join_category jc " +
                "where jc.entity_id = co.id and jc.category_id = ca.id and jc.category_type = 'ACTIVITY') as activityCategories " +
                "from counselings co, clients cl " +
                "where co.client_id = cl.id and cl.status = 'ACTIVE' group by co.id, cl.keyword",
        resultSetMapping = "mapToCounselingForCsv")
@SqlResultSetMapping(name = "mapToCounselingForCsv", classes = @ConstructorResult(targetClass = CounselingForCsvResult.class, columns = {
        @ColumnResult(name = "id", type = UUID.class),
        @ColumnResult(name = "activity", type = String.class),
        @ColumnResult(name = "comment", type = String.class),
        @ColumnResult(name = "counselingDate", type = LocalDateTime.class),
        @ColumnResult(name = "concern", type = String.class),
        @ColumnResult(name = "registeredBy", type = String.class),
        @ColumnResult(name = "keyword", type = String.class),
        @ColumnResult(name = "clientId", type = String.class),
        @ColumnResult(name = "legalCategories", type = String.class),
        @ColumnResult(name = "activityCategories", type = String.class),
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "counseling_date")
    private LocalDateTime counselingDate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "status")
    private String status;

    @Column(name = "required_time")
    private Integer requiredTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case counselingCase;

    @Column(name = "search_vector", columnDefinition = "tsvector")
    @Type(PostgreSQLTSVectorType.class)
    private String searchVector;

    public String getCounselingStatus() {
        return counselingStatus;
    }

    public void setCounselingStatus(String counselingStatus) {
        this.counselingStatus = counselingStatus;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getConcern() {
        return concern;
    }

    public void setConcern(String concern) {
        this.concern = concern;
    }

    public String getConcernCategory() {
        return concernCategory;
    }

    public void setConcernCategory(String concernCategory) {
        this.concernCategory = concernCategory;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(String activityCategory) {
        this.activityCategory = activityCategory;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getCounselingDate() {
        return counselingDate;
    }

    public void setCounselingDate(LocalDateTime counselingDate) {
        this.counselingDate = counselingDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRequiredTime() {
        return requiredTime;
    }

    public void setRequiredTime(Integer requiredTime) {
        this.requiredTime = requiredTime;
    }

    public Case getCounselingCase() {
        return counselingCase;
    }

    public void setCounselingCase(Case counselingCase) {
        this.counselingCase = counselingCase;
    }

    public String getSearchVector() {
        return searchVector;
    }

    public void setSearchVector(String searchVector) {
        this.searchVector = searchVector;
    }
}
