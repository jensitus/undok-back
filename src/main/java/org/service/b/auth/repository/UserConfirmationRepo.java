package org.service.b.auth.repository;

import org.service.b.auth.model.UserConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfirmationRepo extends JpaRepository<UserConfirmation, String> {

  UserConfirmation findByConfirmationTokenAndUserId(String token, Long user_id);

}
