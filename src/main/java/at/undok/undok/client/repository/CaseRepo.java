package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CaseRepo extends JpaRepository<Case, UUID> {

    List<Case> findByClientIdAndStatus(UUID clientId, String status);

    Case findFirstByClientIdOrderByEndDateAsc(UUID clientId);

    @Query(value = """
            select count(distinct ca.id) from cases ca, counselings co, clients cl
                                      where cl.id = co.client_id
                                        and co.case_id = ca.id
                                        and ca.status = 'OPEN'
                                        and cl.id = :client_id
            """, nativeQuery = true)
    Integer countOpenCases(UUID client_id);


}
