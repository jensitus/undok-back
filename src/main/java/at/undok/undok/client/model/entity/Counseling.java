package at.undok.undok.client.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;

@Data
// @Entity
public class Counseling {

    @Id
    private UUID id;

    private String counselingStatus;

    private LocalDate entryDate;

    private String concern;

    private String concernCategory;

    private String activity;

    private String activityCategory;

    private String registeredBy;

}
