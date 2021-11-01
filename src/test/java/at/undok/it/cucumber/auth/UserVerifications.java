package at.undok.it.cucumber.auth;

import at.undok.auth.repository.UserRepo;

import static org.assertj.core.api.Assertions.assertThat;

public class UserVerifications {

    private final UserRepo userRepo;

    public UserVerifications(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void assertUserConfirmationStatus(String username, UserConfirmationStatus expected) {
        var user = userRepo.findByUsername(username);
        assertThat(user)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("confirmed", expected.equals(UserConfirmationStatus.CONFIRMED));
    }
}
