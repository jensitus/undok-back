package at.undok.undok.client.model.entity;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "persons")
public class Person extends AbstractCrud implements Serializable {

    /* here we have to clear if it sex or gender */
//    private String sex;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY)
    private Employer employer;

//    @OneToMany
//    private Set<Contact> contacts;

    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "contact_data")
    private String contactData;

    @Column(name ="email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", dateOfBirth=" + dateOfBirth +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
    }
}
