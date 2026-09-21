package at.undok.undok.client.repository;

import at.undok.undok.client.model.dto.ClientCaseProjection;
import at.undok.undok.client.model.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CaseRepo extends JpaRepository<Case, UUID> {

    List<Case> findByClientIdAndStatus(UUID clientId, String status);

    /**
     * Ordered so callers can take the last element as the most recently closed case. The
     * unordered variant leaves that to whatever order Postgres happens to return.
     */
    List<Case> findByClientIdAndStatusOrderByEndDateAsc(UUID clientId, String status);

    Case findFirstByClientIdOrderByEndDateAsc(UUID clientId);

    @Query(value = """
            select count(distinct ca.id) from cases ca, counselings co, clients cl
                                      where cl.id = co.client_id
                                        and co.case_id = ca.id
                                        and ca.status = 'OPEN'
                                        and cl.id = :client_id
            """, nativeQuery = true)
    Integer countOpenCases(UUID client_id);

    /**
     * The one case that currently represents each of the given clients: the OPEN one if there
     * is one, otherwise the most recently closed one. DISTINCT ON collapses clients that have
     * several cases down to that single row.
     * <p>
     * Unlike {@link #countOpenCases}, this joins straight on cases.client_id rather than through
     * counselings, so a case that has no counseling yet is still visible.
     * <p>
     * Aliases are quoted so Postgres preserves the camelCase labels the projection getters are
     * matched against.
     */
    @Query(value = """
            SELECT DISTINCT ON (ca.client_id)
                   ca.client_id   AS "clientId",
                   ca.id          AS "caseId",
                   ca.status      AS "status",
                   ca.start_date  AS "startDate",
                   ca.end_date    AS "endDate",
                   ca.referred_to AS "referredTo"
            FROM cases ca
            WHERE ca.client_id IN (:clientIds)
            ORDER BY ca.client_id,
                     CASE WHEN ca.status = 'OPEN' THEN 0 ELSE 1 END,
                     ca.end_date DESC NULLS FIRST,
                     ca.start_date DESC
            """, nativeQuery = true)
    List<ClientCaseProjection> findCurrentCaseForClients(@Param("clientIds") List<UUID> clientIds);

}
