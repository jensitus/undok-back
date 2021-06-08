package at.undok.auth.repository;

import at.undok.auth.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);
  Boolean existsByUsername(String username);
  Boolean existsByEmail(String email);
  List<User> findAll();
  User findByEmail(String email);
  User findByEmailAndConfirmationToken(String email, String confirmationToken);

  @Query("select u.confirmationTokenCreatedAt from User u where u.email = :email and u.confirmationToken = :confirmationToken")
  LocalDateTime selectConfirmationTokenCreatedAt(String email, String confirmationToken);

  @Query("select u.changePassword from User u where u.email = :email and u.confirmationToken = :confirmationToken")
  boolean selectChangePasswordFromUser(String email, String confirmationToken);

}
