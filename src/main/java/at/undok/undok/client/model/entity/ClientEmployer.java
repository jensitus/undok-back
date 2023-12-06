package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "client_employer")
@Data
@ToString
public class ClientEmployer extends AbstractCrud {

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "employer_id")
    private UUID employerId;

    @Column(name = "start")
    private LocalDate from;

    @Column(name = "finished")
    private LocalDate until;

    @Column(name = "industry")
    private String industry;

    @Column(name = "industry_sub")
    private String industrySub;

    @Column(name = "job_function")
    private String jobFunction;

    @Column(name = "job_remarks")
    private String jobRemarks;

}
