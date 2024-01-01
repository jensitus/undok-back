package at.undok.undok.client.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ClientForTableDto {
    private UUID clientId;
    private String firstName;
    private String lastName;
    private String keyword;
    private String nationality;
    private String sector;
    private String currentResidentStatus;
}
