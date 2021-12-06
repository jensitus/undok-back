package at.undok.undok.client.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmployerDto extends AbstractDto {

    private PersonDto person;
    private String company;
    private String position;
    private List<ClientDto> clients;

}
