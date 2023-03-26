package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import at.undok.common.util.UUIDConverter;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = JoinCategory.TABLE_NAME)
@Entity
@Data
public class JoinCategory {

    public static final String TABLE_NAME = "join_category";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "category_type")
    private String categoryType;

    @Column(name = "entity_type")
    private String entityType;

}
