package at.undok.undok.client.model.dto;

import java.util.UUID;

/**
 * One join_category row flattened onto the client that owns the case it hangs off.
 * Used to batch-load CASE-scoped categories for a whole client list in a single query,
 * instead of going through getClientById per client.
 */
public interface ClientCategoryProjection {

    UUID getClientId();

    UUID getCategoryId();

    String getName();

    String getType();

}
