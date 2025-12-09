package at.undok.undok.client.repository;

import at.undok.undok.client.model.dto.CounselingForCsvResult;
import at.undok.undok.client.model.entity.Counseling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CounselingRepo extends JpaRepository<Counseling, UUID> {

    @Query("select c from Counseling c where c.counselingDate >= :today order by c.counselingDate asc")
    List<Counseling> findAllInFuture(LocalDateTime today);

    @Query("select c from Counseling c where c.counselingDate <= :today")
    List<Counseling> findAllInPast(LocalDateTime today);

    List<Counseling> findByStatusOrderByCounselingDateDesc(String status);

    long countByStatus(String status);

    List<Counseling> findByCounselingDateIsNull();

    @Query(name = "getCounselingsForCsv", nativeQuery = true)
    List<CounselingForCsvResult> getCounselingForCsv();


    @Query(value = """
            select distinct ca.id from cases ca, counselings co, clients cl
                                      where cl.id = co.client_id
                                        and co.case_id = ca.id
                                        and ca.status = 'OPEN'
                                        and cl.id = :client_id
            """, nativeQuery = true)
    UUID findCaseId(UUID client_id);

    @Query(value = """
            select SUM(c.required_time) from counselings c where c.case_id = :caseId
            """,
            nativeQuery = true)
    Integer selectTotalConsultationTime(UUID caseId);

    List<Counseling> findByClientIdOrderByCounselingDateDesc(UUID clientId);

    List<Counseling> findByClientIdOrderByCounselingDateAsc(UUID clientId);

    // Count of counselings between a given date range [from, to)
    long countByCounselingDateGreaterThanEqualAndCounselingDateLessThan(LocalDateTime from, LocalDateTime to);

//    @Query(value = """
//        SELECT c.*
//        FROM counselings c
//        WHERE c.search_vector @@ plainto_tsquery('german', :searchTerm)
//          AND (CAST (:dateFrom AS TIMESTAMP) IS NULL OR c.counseling_date >= :dateFrom)
//          AND (CAST (:dateTo AS TIMESTAMP) IS NULL OR c.counseling_date < :dateTo)
//        ORDER BY ts_rank(c.search_vector, plainto_tsquery('german', :searchTerm)) DESC
//        """,
//            countQuery = """
//        SELECT COUNT(*)
//        FROM counselings c
//        WHERE c.search_vector @@ plainto_tsquery('german', :searchTerm)
//          AND (CAST (:dateFrom AS TIMESTAMP) IS NULL OR c.counseling_date >= :dateFrom)
//          AND (CAST (:dateTo AS TIMESTAMP) IS NULL OR c.counseling_date < :dateTo)
//       \s""",
//            nativeQuery = true)
//    Page<Counseling> fullTextSearch(@Param("searchTerm") String searchTerm,
//                                    @Param("dateFrom") LocalDateTime dateFrom,
//                                    @Param("dateTo") LocalDateTime dateTo,
//                                    Pageable pageable
//    );

    /**
     * Full-text search across concern and activity fields
     * Uses PostgreSQL's ts_rank for relevance scoring
     */
    @Query(value = """
        SELECT c.*, ts_rank(c.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM counselings c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        ORDER BY rank DESC
        """, nativeQuery = true)
    List<Counseling> fullTextSearch(@Param("searchTerm") String searchTerm);

    /**
     * Full-text search with date range filter
     */
    @Query(value = """
        SELECT c.*, ts_rank(c.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM counselings c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        AND c.counseling_date >= :startDate
        AND c.counseling_date <= :endDate
        ORDER BY rank DESC
        """, nativeQuery = true)
    List<Counseling> fullTextSearchWithDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Full-text search with pagination support
     */
    @Query(value = """
        SELECT c.*, ts_rank(c.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM counselings c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        ORDER BY rank DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Counseling> fullTextSearchWithPagination(
            @Param("searchTerm") String searchTerm,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    /**
     * Full-text search with pagination and date range
     */
    @Query(value = """
        SELECT c.*, ts_rank(c.search_vector, websearch_to_tsquery('german', :searchTerm)) AS rank
        FROM counselings c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        AND c.counseling_date >= :startDate
        AND c.counseling_date <= :endDate
        ORDER BY rank DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Counseling> fullTextSearchWithPaginationAndDateRange(
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
        FROM counselings c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        AND c.counseling_date >= :startDate
        AND c.counseling_date <= :endDate
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
        FROM counselings c
        WHERE c.search_vector @@ websearch_to_tsquery('german', :searchTerm)
        """, nativeQuery = true)
    long countFullTextSearch(@Param("searchTerm") String searchTerm);

}
