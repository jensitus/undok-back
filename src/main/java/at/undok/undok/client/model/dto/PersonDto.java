package at.undok.undok.client.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PersonDto {

    private UUID id;

    private String type;

    private String sex;

    private String dateOfBirth;

    private String lastName;

    private String firstName;

    private AddressDto address;

}
