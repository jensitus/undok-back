package at.undok.undok.client.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddressDto {

    private UUID id;

    private String street;

    private String plz;

    private String city;

    private String county;

    private String country;

}
