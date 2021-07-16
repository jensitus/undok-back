package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Counseling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CounselingRepo extends JpaRepository<Counseling, UUID> {

    @Query("select c from Counseling c where c.counselingDate >= :today")
    List<Counseling> findAllInFuture(LocalDateTime today);

    @Query("select c from Counseling c where c.counselingDate <= :today")
    List<Counseling> findAllInPast(LocalDateTime today);

}
