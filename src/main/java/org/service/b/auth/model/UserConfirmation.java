package org.service.b.auth.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.service.b.common.util.UUIDConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_confirmation")
public class UserConfirmation {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", unique = true, nullable = false)
  private UUID id;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @Column(name = "confirmation_token")
  private String confirmationToken;

  @Column(name = "confirmation_expiry")
  private LocalDateTime confirmationExpiry;

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt;

  public UserConfirmation() {
  }

}
