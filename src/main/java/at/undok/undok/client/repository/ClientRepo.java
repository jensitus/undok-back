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

}
