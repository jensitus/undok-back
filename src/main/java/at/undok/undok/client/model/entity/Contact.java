package at.undok.undok.client.model.entity;

import at.undok.undok.client.model.enumeration.ContactType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Data
@Entity
public class Contact {

    @Id
    private UUID id;

    @OneToOne
    private Person person;

    private ContactType contactType;

    private String value;

}
