package at.undok.undok.client.model.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "counseling")
public class Counseling {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", unique = true, nullable = false, insertable = false, updatable = false)
    private UUID id;

    private String counselingStatus;

    private LocalDate entryDate;

    private String concern;

    private String concernCategory;

    private String activity;

    private String activityCategory;

    private String registeredBy;

}
