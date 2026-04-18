package at.undok.undok.client.repository;

import at.undok.undok.client.model.dto.KeyCountProjection;
import at.undok.undok.client.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepo extends JpaRepository<Client, UUID> {

    List<Client> findByKeyword(String keyword);

    boolean existsByKeyword(String keyword);

    List<Client> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);


    @Query(value = """
            select count(*) from (
                select cl.*
                from clients cl, counselings co
                where cl.id = co.client_id
                  and co.counseling_date >= :from
                  and co.counseling_date < :to
                except
                select cl.*
                from clients cl, counselings co
                where cl.id = co.client_id
                  and co.counseling_date < :from
            ) as sub
            """, nativeQuery = true)
    long countClientsWithFirstCounselingInRange(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(cl.language, 'unbekannt') as key, COUNT(co) as count " +
            "FROM Client cl JOIN cl.counselings co " +
            "WHERE cl.interpreterNecessary != true " +
            "AND co.counselingDate >= :fromDate " +
            "AND co.counselingDate < :toDate " +
            "GROUP BY cl.language")
    List<KeyCountProjection> countByLanguageInDateRange(
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    @Query("SELECT COALESCE(cl.nationality, 'keine Angabe') as key, COUNT(DISTINCT cl.id) as count " +
            "FROM Client cl JOIN cl.counselings co " +
            "WHERE co.counselingDate >= :fromDate " +
            "AND co.counselingDate < :toDate " +
            "GROUP BY cl.nationality")
    List<KeyCountProjection> countByNationalityInDateRange(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("SELECT COALESCE(cl.gender, 'unbekannt') as key, COUNT(DISTINCT cl.id) as count " +
            "FROM Client cl " +
            "JOIN cl.counselings co " +
            "WHERE co.counselingDate >= :fromDate " +
            "AND co.counselingDate < :toDate " +
            "GROUP BY cl.gender")
    List<KeyCountProjection> countByGenderInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                                      @Param("toDate") LocalDateTime toDate);

    @Query("select COALESCE(cl.sector, 'unbekannt') as key, count(distinct cl.id) as count " +
            "from Client cl JOIN cl.counselings co" +
            "  where co.counselingDate >= :fromDate" +
            "  and co.counselingDate < :toDate" +
            " group by cl.sector")
    List<KeyCountProjection> countBySectorInDateRange(@Param("fromDate") LocalDateTime fromDate,
                                                      @Param("toDate") LocalDateTime toDate);

    @Query(value = "SELECT ca.name as key, COUNT(*) as count " +
            "FROM categories ca " +
            "JOIN join_category jc ON ca.id = jc.category_id " +
            "JOIN counselings co ON co.id = jc.entity_id " +
            "WHERE ca.type = 'ACTIVITY' " +
            "AND jc.entity_type = 'COUNSELING' " +
            "AND co.counseling_date >= :fromDate " +
            "AND co.counseling_date < :toDate " +
            "GROUP BY ca.name",
            nativeQuery = true)
    List<KeyCountProjection> countByActivityInDateRange(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    /**
     * Full-text search across keyword, last_name, first_name, and comment fields
     * Uses PostgreSQL's ts_rank for relevance scoring
     */
    @Query(value = """
        SELECT c.*, ts_rank(c.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM clients c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        ORDER BY rank DESC
        """, nativeQuery = true)
    List<Client> fullTextSearch(@Param("searchTerm") String searchTerm);

    /**
     * Full-text search with date range filter (uses created_at)
     */
    @Query(value = """
        SELECT c.*, ts_rank(c.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM clients c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        AND c.created_at >= :startDate
        AND c.created_at <= :endDate
        ORDER BY rank DESC
        """, nativeQuery = true)
    List<Client> fullTextSearchWithDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Full-text search with pagination support
     */
    @Query(value = """
        SELECT c.*, ts_rank(c.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM clients c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        ORDER BY rank DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Client> fullTextSearchWithPagination(
            @Param("searchTerm") String searchTerm,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /**
     * Full-text search with pagination and date range
     */
    @Query(value = """
        SELECT c.*, ts_rank(c.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM clients c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        AND c.created_at >= :startDate
        AND c.created_at <= :endDate
        ORDER BY rank DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Client> fullTextSearchWithPaginationAndDateRange(
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
        FROM clients c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        AND c.created_at >= :startDate
        AND c.created_at <= :endDate
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
        FROM clients c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        """, nativeQuery = true)
    long countFullTextSearch(@Param("searchTerm") String searchTerm);

    /**
     * Find clients connected to categories of a specific type matching the search term.
     * Categories are linked to cases, which are linked to clients.
     *
     * @param searchTerm the search term to match against category names
     * @param categoryType the category type (e.g., INDUSTRY_UNION, COUNSELING_LANGUAGE, ORIGIN_OF_ATTENTION)
     */
    @Query(value = """
        SELECT DISTINCT c.*
        FROM clients c
        JOIN cases ca ON ca.client_id = c.id
        JOIN join_category jc ON jc.entity_id = ca.id
        JOIN categories cat ON jc.category_id = cat.id
        WHERE jc.entity_type = 'CASE'
          AND jc.category_type = :categoryType
          AND LOWER(cat.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        """, nativeQuery = true)
    List<Client> findClientsByCategoryName(
            @Param("searchTerm") String searchTerm,
            @Param("categoryType") String categoryType);

    /**
     * Find clients connected to categories of specific types matching the search term.
     * Categories are linked to cases, which are linked to clients.
     *
     * @param searchTerm the search term to match against category names
     * @param categoryTypes list of category types to search
     */
    @Query(value = """
        SELECT DISTINCT c.*
        FROM clients c
        JOIN cases ca ON ca.client_id = c.id
        JOIN join_category jc ON jc.entity_id = ca.id
        JOIN categories cat ON jc.category_id = cat.id
        WHERE jc.entity_type = 'CASE'
          AND jc.category_type IN (:categoryTypes)
          AND LOWER(cat.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        UNION
        SELECT DISTINCT c.*
        FROM clients c
        JOIN counselings co ON co.client_id = c.id
        JOIN join_category jc ON jc.entity_id = co.id
        JOIN categories cat ON jc.category_id = cat.id
        WHERE jc.entity_type = 'COUNSELING'
          AND jc.category_type IN (:categoryTypes)
          AND LOWER(cat.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        """, nativeQuery = true)
    List<Client> findClientsByCategoryNames(
            @Param("searchTerm") String searchTerm,
            @Param("categoryTypes") List<String> categoryTypes);

    /**
     * Projection interface for client ID with matched category name
     */
    interface ClientCategoryMatch {
        UUID getClientId();
        String getCategoryName();
        String getCategoryType();
    }

    /**
     * Find matching category names for clients.
     * Returns pairs of (client_id, category_name) for building the matchedCategories list.
     */
    @Query(value = """
        SELECT c.id as clientId, cat.name as categoryName, jc.category_type as categoryType
        FROM clients c
        JOIN cases ca ON ca.client_id = c.id
        JOIN join_category jc ON jc.entity_id = ca.id
        JOIN categories cat ON jc.category_id = cat.id
        WHERE jc.entity_type = 'CASE'
          AND jc.category_type IN (:categoryTypes)
          AND LOWER(cat.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        UNION
        SELECT c.id as clientId, cat.name as categoryName, jc.category_type as categoryType
        FROM clients c
        JOIN counselings co ON co.client_id = c.id
        JOIN join_category jc ON jc.entity_id = co.id
        JOIN categories cat ON jc.category_id = cat.id
        WHERE jc.entity_type = 'COUNSELING'
          AND jc.category_type IN (:categoryTypes)
          AND LOWER(cat.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        """, nativeQuery = true)
    List<ClientCategoryMatch> findMatchedCategoriesForClients(
            @Param("searchTerm") String searchTerm,
            @Param("categoryTypes") List<String> categoryTypes);

}
