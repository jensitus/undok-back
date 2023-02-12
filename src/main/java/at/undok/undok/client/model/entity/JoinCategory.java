package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import at.undok.common.util.UUIDConverter;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Table(name = JoinCategory.TABLE_NAME)
@Entity
@Data
public class JoinCategory extends AbstractCrud {

    public static final String TABLE_NAME = "join_category";

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "category_type")
    private String categoryType;

    @Column(name = "entity_type")
    private String entityType;

}
