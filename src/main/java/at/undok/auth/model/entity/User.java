package at.undok.auth.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    @NaturalId
    @Email
    private String email;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "confirmed")
    private Boolean confirmed;

    @Column(name = "confirmation_token")
    @JsonIgnore
    private String confirmationToken;

    @Column(name = "confirmation_token_created_at")
    @JsonIgnore
    private LocalDateTime confirmationTokenCreatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "admin")
    private boolean admin;

    @Column(name = "change_password")
    private boolean changePassword;

    public User() {
    }

    public User(String username, String email, String password, String confirmationToken, LocalDateTime confirmationTokenCreatedAt, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmationToken = confirmationToken;
        this.confirmationTokenCreatedAt = confirmationTokenCreatedAt;
        this.createdAt = createdAt;
    }

    public User(String username, String email, String confirmationToken, LocalDateTime confirmationTokenCreatedAt, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.confirmationToken = confirmationToken;
        this.createdAt = createdAt;
    }
}
