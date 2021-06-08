package at.undok.auth.repository;

import at.undok.auth.model.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Long> {
  PasswordResetToken findByTokenAndUserId(String token, UUID user_id);
}
