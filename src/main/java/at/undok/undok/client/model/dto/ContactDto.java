package at.undok.undok.client.model.dto;

import at.undok.undok.client.model.enumeration.ContactType;
import lombok.Data;

import java.util.UUID;

@Data
public class ContactDto {

    private UUID id;

    private PersonDto person;

    private ContactType contactType;

    private String value;

}
