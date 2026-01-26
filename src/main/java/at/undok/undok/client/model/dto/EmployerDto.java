package at.undok.undok.client.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmployerDto extends AbstractDto {

    private String company;
    private String position;
    private List<ClientDto> clients;

    // Fields previously in PersonDto
    private LocalDate dateOfBirth;
    private String lastName;
    private String firstName;
    private String email;
    private String telephone;
    private String gender;
    private String contactData;

    // Fields previously in AddressDto
    private String street;
    private String zipCode;
    private String city;
    private String country;

}
