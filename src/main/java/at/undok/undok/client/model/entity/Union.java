package at.undok.undok.client.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
// @Entity
public class Union {

    @Id
    private Long id;

    private String name;

}
