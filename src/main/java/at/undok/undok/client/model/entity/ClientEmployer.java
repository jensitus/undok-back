package at.undok.undok.client.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "client_employer")
@Data
public class ClientEmployer extends AbstractCrud {

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "employer_id")
    private UUID employerId;

//    @Column(name = "from")
//    private LocalDate from;
//
//    @Column(name = "until")
//    private LocalDate until;

}
