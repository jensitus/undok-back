package at.undok.undok.client.repository;

import at.undok.undok.client.model.dto.CounselingForCsvResult;
import at.undok.undok.client.model.entity.Counseling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
