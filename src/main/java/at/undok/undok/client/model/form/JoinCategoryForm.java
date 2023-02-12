package at.undok.undok.client.model.form;

import lombok.Data;

import java.util.UUID;

@Data
public class JoinCategoryForm {

    private UUID categoryId;
    private UUID entityId;
    private String categoryType;
    private String entityType;
}
