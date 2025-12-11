package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepo extends JpaRepository<Task, UUID>  {

    List<Task> findByCaseEntity_Id(UUID caseId);
    List<Task> findByStatusIn(List<String> statuses);

    /**
     * Full-text search across concern and activity fields
     * Uses PostgreSQL's ts_rank for relevance scoring
     */
    @Query(value = """
        SELECT t.*, ts_rank(t.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM tasks t
        WHERE t.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        ORDER BY rank DESC
        """, nativeQuery = true)
    List<Task> fullTextSearch(@Param("searchTerm") String searchTerm);

    /**
     * Full-text search with date range filter
     */
    @Query(value = """
        SELECT t.*, ts_rank(t.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM tasks t
        WHERE t.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        AND t.created_at >= :startDate
        AND t.created_at <= :endDate
        ORDER BY rank DESC
        """, nativeQuery = true)
    List<Task> fullTextSearchWithDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Full-text search with pagination support
     */
    @Query(value = """
        SELECT t.*, ts_rank(t.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM tasks t
        WHERE t.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        ORDER BY rank DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Task> fullTextSearchWithPagination(
            @Param("searchTerm") String searchTerm,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /**
     * Full-text search with pagination and date range
     */
    @Query(value = """
        SELECT t.*, ts_rank(t.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM tasks t
        WHERE t.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        AND t.created_at >= :startDate
        AND t.created_at <= :endDate
        ORDER BY rank DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Task> fullTextSearchWithPaginationAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /**
     * Count results with date range for pagination
     */
    @Query(value = """
        SELECT COUNT(*)
        FROM tasks t
        WHERE t.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        AND t.created_at >= :startDate
        AND t.created_at <= :endDate
        """, nativeQuery = true)
    long countFullTextSearchWithDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Count results for pagination
     */
    @Query(value = """
        SELECT COUNT(*)
        FROM tasks t
        WHERE t.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        """, nativeQuery = true)
    long countFullTextSearch(@Param("searchTerm") String searchTerm);

}
