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

    public Case(String name, String status, UUID clientId) {
        this.name = name;
        this.status = status;
        this.clientId = clientId;
    }
}
