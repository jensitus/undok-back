package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import at.undok.undok.client.model.dto.CheckClientEmployerDto;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDate;

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

    @Column(name = "company")
    private String company;

    @Column(name = "position")
    private String position;

    @Column(name = "status")
    private String status;

    // Fields previously in Person
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "gender")
    private String gender;

    @Column(name = "contact_data")
    private String contactData;

    // Fields previously in Address
    @Column(name = "street")
    private String street;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

}
