package at.undok.undok.client.model.dto;

import at.undok.undok.client.model.enumeration.MaritalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class InterimClientDto {

    private UUID id;

    private String type;

    private LocalDate dateOfBirth;

    private String lastName;

    private String firstName;

    private String keyword;

    private String education;

    private MaritalStatus maritalStatus;

    private Boolean interpreterNecessary;

    private String howHasThePersonHeardFromUs;

    private Boolean vulnerableWhenAssertingRights;

}
