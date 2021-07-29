package at.undok.undok.client.model.dto;

import at.undok.undok.client.model.entity.Person;
import lombok.Data;

@Data
public class EmployerDto extends AbstractDto {

    private PersonDto person;
    private String company;
    private String position;

}
