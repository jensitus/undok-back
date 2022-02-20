package at.undok.undok.client.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "categories")
@Data
public class Category extends AbstractCrud {

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

}
