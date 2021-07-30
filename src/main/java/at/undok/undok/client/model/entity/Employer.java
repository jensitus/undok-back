package at.undok.undok.client.model.entity;

import liquibase.pro.packaged.C;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = Employer.TABLE_NAME)
public class Employer extends AbstractCrud {

    public static final String TABLE_NAME = "employers";

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="person_id", referencedColumnName = "id")
    private Person person;

    @Column(name = "company")
    private String company;

    @Column(name = "position")
    private String position;





}
