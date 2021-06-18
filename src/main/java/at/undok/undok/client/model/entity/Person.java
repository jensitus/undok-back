package at.undok.undok.client.model.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.util.UUID;

@Data
@MappedSuperclass
public class Person {

    @Id
    private UUID personId;

    private String type;

    private String sex;

    private String dateOfBirth;

    private String lastName;

    private String firstName;

    @OneToOne
    private Address address;


}
