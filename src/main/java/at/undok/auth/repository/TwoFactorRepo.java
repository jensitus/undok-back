package at.undok.auth.repository;

import at.undok.auth.model.entity.TwoFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TwoFactorRepo extends JpaRepository<TwoFactor, UUID> {

    TwoFactor findByUserIdAndToken(UUID userId, String token);

}
