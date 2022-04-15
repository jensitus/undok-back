package at.undok.auth.model.entity;

import at.undok.common.model.AbstractCrud;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "two_factor")
public class TwoFactor extends AbstractCrud {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration")
    private LocalDateTime expiration;

}
