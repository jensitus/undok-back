package at.undok.undok.client.model.entity;

import lombok.Getter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "fully")
@Immutable
@Getter
public class Fully {

    @Id
    private UUID id;
    private String firstname;
    private String lastname;
    private String keyword;
    private String c_id;
    private String p_client_id;

}
