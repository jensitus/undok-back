package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import at.undok.undok.client.model.dto.CheckClientEmployerDto;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = Employer.TABLE_NAME)
@NamedNativeQuery(name = "checkRelationEmployerClient", query =
        "select count(*) as count from employers e, client_employer ce, clients c " +
                "where e.id = ce.employer_id and c.id = ce.client_id and e.id = :employerId " +
                "and c.status = :status",
        resultSetMapping = "mapToCheckClientEmployer")
@SqlResultSetMapping(name = "mapToCheckClientEmployer",
        classes = {@ConstructorResult(
                targetClass = CheckClientEmployerDto.class,
                columns = {
                        @ColumnResult(name = "count", type = Long.class)})})
public class Employer extends AbstractCrud {

    public static final String TABLE_NAME = "employers";

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @Column(name = "company")
    private String company;

    @Column(name = "position")
    private String position;

    @Column(name = "status")
    private String status;

}
