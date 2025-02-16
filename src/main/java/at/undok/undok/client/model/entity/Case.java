package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cases")
public class Case extends AbstractCrud {

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "counselingCase", fetch = FetchType.LAZY)
    private List<Counseling> counselings;

    @Column(name = "status")
    private String status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "referred_to")
    private String referredTo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Counseling> getCounselings() {
        return counselings;
    }

    public void setCounselings(List<Counseling> counselings) {
        this.counselings = counselings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getReferredTo() {
        return referredTo;
    }

    public void setReferredTo(String referredTo) {
        this.referredTo = referredTo;
    }
}
