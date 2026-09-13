package at.undok.undok.client.repository;

import at.undok.undok.client.model.dto.ClientCategoryProjection;
import at.undok.undok.client.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, UUID> {

    @Query("select c from Category c where c.type = :type and c.toBeDeleted = :toBeDeleted order by c.name asc")
    List<Category> getCategoriesByType(String type, Boolean toBeDeleted);

    boolean existsByName(String name);

    @Query("select c from Category c, JoinCategory jc " +
            "where jc.categoryType = :categoryType " +
            "and jc.entityId = :entityId " +
            "and c.id = jc.categoryId ")
    List<Category> getCategoryByTypeAndEntity(String categoryType, UUID entityId);

    /**
     * Batch variant of {@link #getCategoryByTypeAndEntity}: resolves a CASE-scoped category type
     * for many clients at once, keyed by client rather than by case. Aliases are quoted so
     * Postgres preserves the camelCase labels the projection getters are matched against.
     */
    @Query(value = """
            SELECT ca.client_id AS "clientId",
                   cat.id       AS "categoryId",
                   cat.name     AS "name",
                   cat.type     AS "type"
            FROM cases ca
            JOIN join_category jc ON jc.entity_id = ca.id
            JOIN categories cat ON cat.id = jc.category_id
            WHERE jc.entity_type = 'CASE'
              AND jc.category_type = :categoryType
              AND ca.status = :caseStatus
              AND ca.client_id IN (:clientIds)
            ORDER BY cat.name ASC
            """, nativeQuery = true)
    List<ClientCategoryProjection> findCaseCategoriesByTypeForClients(
            @Param("categoryType") String categoryType,
            @Param("caseStatus") String caseStatus,
            @Param("clientIds") List<UUID> clientIds);

    List<Category> findCategoryByToBeDeleted(Boolean toBeDeleted);

    Category findByNameAndType(String name, String type);
}
