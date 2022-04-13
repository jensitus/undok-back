package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = Employer.TABLE_NAME)
public class Employer extends AbstractCrud {

    public static final String TABLE_NAME = "employers";

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="person_id", referencedColumnName = "id")
    private Person person;

    @Column(name = "company")
    private String company;

    @Column(name = "position")
    private String position;

}
