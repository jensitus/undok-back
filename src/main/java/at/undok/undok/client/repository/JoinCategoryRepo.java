package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.JoinCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JoinCategoryRepo extends JpaRepository<JoinCategory, UUID> {

    List<JoinCategory> findByEntityIdAndCategoryType(UUID entityId, String categoryType);

    void deleteAllByEntityId(UUID entityId);

    JoinCategory findByEntityTypeAndEntityIdAndCategoryTypeAndCategoryId(String entityType, UUID entityId, String categoryType, UUID categoryId);

}
