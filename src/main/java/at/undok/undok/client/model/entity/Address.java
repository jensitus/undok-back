package at.undok.undok.client.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity
public class Address {

    @Id
    private UUID id;

    private String street;

    private String plz;

    private String city;

    private String county;

    private String country;

}
