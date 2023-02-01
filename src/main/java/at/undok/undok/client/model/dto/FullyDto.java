package at.undok.undok.client.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class FullyDto {
    private UUID id;
    private String firstname;
    private String lastname;
    private String keyword;
    private String c_id;
    private String p_client_id;
}
