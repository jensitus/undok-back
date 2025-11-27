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

    @Query("SELECT COALESCE(p.gender, 'unbekannt') as key, COUNT(DISTINCT p.id) as count " +
            "FROM Client cl " +
            "JOIN cl.counselings co " +
            "JOIN cl.person p " +
            "WHERE co.counselingDate >= :fromDate " +
            "AND co.counselingDate < :toDate " +
            "GROUP BY p.gender")
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

}
