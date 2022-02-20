package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, UUID> {

    @Query("select c from Category c where c.type = :type")
    List<Category> getCategoriesByType(String type);

}
