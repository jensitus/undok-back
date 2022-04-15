package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "categories")
@Data
public class Category extends AbstractCrud {

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "type")
    private String type;

}
