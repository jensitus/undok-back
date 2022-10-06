package at.undok.it.cucumber;

import at.undok.auth.model.dto.SignUpDto;
import com.github.javafaker.Faker;

import java.util.Set;

public class UndokTestData {

    private final Faker faker;

    public UndokTestData(Faker faker) {
        this.faker = faker;
    }

    public SignUpDto newRegistration() {
        var registration = new SignUpDto();
        registration.setAdmin(false);
        registration.setEmail(email());
        registration.setUsername(userName());
        registration.setPassword(securePassword());
        registration.setRole(Set.of("ROLE_USER"));
        registration.setPasswordConfirmation(registration.getPassword());
        return registration;
    }

    private String securePassword() {
        return faker.internet().password(10, 40, true, true);
    }

    public String email() {
        return faker.internet().emailAddress();
    }

    public String userName() {
        return faker.name().username();
    }
}
