package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
// @Entity
// @Table(name = "cases")
public class Case extends AbstractCrud {

    // @OneToMany(mappedBy = "cases", fetch = FetchType.EAGER)
    private List<Counseling> counselings;

    //
    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
