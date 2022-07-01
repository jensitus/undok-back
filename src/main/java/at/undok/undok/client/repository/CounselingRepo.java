package at.undok.undok.client.repository;

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

    List<Counseling> findAllByOrderByCounselingDateDesc();

}
