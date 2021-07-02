package at.undok.undok.client.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
// @Entity
public class Reference {

    @Id
    private Long id;

    private LocalDate dateBegin;

    private LocalDate dateEnd;

    private String refType;

    private int prio;

    private String code;

    private String label;

    private String value;

    private String description;

}
