package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    List<Category> findCategoryByToBeDeleted(Boolean toBeDeleted);

}
