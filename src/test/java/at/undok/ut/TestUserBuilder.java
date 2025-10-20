package at.undok.ut;

import at.undok.auth.model.entity.Role;
import at.undok.auth.model.entity.RoleName;
import at.undok.auth.model.entity.User;
import at.undok.auth.repository.RoleRepo;
import at.undok.auth.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class TestUserBuilder {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createTestUser(String username, String email, String password) {
        createTestUser(username, email, password, false, false);
    }

    public User createTestUser(String username, String email, String password,
                               boolean isAdmin, boolean isLocked) {
        // Ensure role exists
        Role userRole = roleRepo.findByName(RoleName.ROLE_USER)
                                .orElseGet(() -> {
                                    Role role = new Role();
                                    role.setName(RoleName.ROLE_USER);
                                    return roleRepo.save(role);
                                });

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));  // Encrypt password
        user.setConfirmed(true);
        user.setLocked(isLocked);
        user.setAdmin(isAdmin);
        user.setChangePassword(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setConfirmationTokenCreatedAt(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        if (isAdmin) {
            Role adminRole = roleRepo.findByName(RoleName.ROLE_ADMIN)
                                     .orElseGet(() -> {
                                         Role role = new Role();
                                         role.setName(RoleName.ROLE_ADMIN);
                                         return roleRepo.save(role);
                                     });
            roles.add(adminRole);
        }

        user.setRoles(roles);

        return userRepo.save(user);
    }

    public User createAdminUser() {
        return createTestUser("test_admin", "admin@test.com", "admin123", true, false);
    }

    public User createLockedUser() {
        return createTestUser("locked", "locked@test.com", "locked123", false, true);
    }
}
