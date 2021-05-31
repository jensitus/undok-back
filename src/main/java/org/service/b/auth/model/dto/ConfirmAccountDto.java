package org.service.b.auth.model.dto;

import lombok.ToString;
import org.service.b.auth.validation.NewPasswordMatch;
import org.service.b.auth.validation.annotation.PasswordMatches;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@ToString
@PasswordMatches
public class ConfirmAccountDto implements NewPasswordMatch {

    private String confirmationToken;

    private String email;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 70)
    private String password;

    private String passwordConfirmation;

    private String oldPassword;

//    @Override
//    public String getPassword() {
//        return this.password;
//    }
//
//    @Override
//    public String getPasswordConfirmation() {
//        return this.passwordConfirmation;
//    }


    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
