package org.service.b.auth.repository;

import org.service.b.auth.model.UserConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserConfirmationRepo extends JpaRepository<UserConfirmation, String> {

  UserConfirmation findByConfirmationTokenAndUserId(String token, UUID user_id);

}
