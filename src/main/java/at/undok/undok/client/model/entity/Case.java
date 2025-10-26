package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cases")
@NoArgsConstructor
@Data
public class Case extends AbstractCrud {

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "counselingCase", fetch = FetchType.LAZY)
    private List<Counseling> counselings;

    @JsonIgnore
    @OneToMany(mappedBy = "caseEntity", fetch = FetchType.LAZY)
    private List<Task> tasks;

    @Column(name = "status")
    private String status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "referred_to")
    private String referredTo;

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "total_consultation_time")
    private Integer totalConsultationTime;

    @Column(name = "target_group")
    private String targetGroup;

    @Column(name = "human_trafficking")
    private Boolean humanTrafficking;

    @Column(name = "job_center_block")
    private Boolean jobCenterBlock;

    @Column(name = "working_relationship")
    private String workingRelationship;

    public Case(String name, String status, UUID clientId) {
        this.name = name;
        this.status = status;
        this.clientId = clientId;
    }
}
