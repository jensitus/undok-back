package at.undok.auth.model.form;

import lombok.Data;
import lombok.ToString;
import at.undok.auth.validation.annotation.ValidEmail;

import javax.validation.constraints.NotBlank;

@Data
@ToString
public class CreateUserForm {

    @NotBlank
    private String username;

    @NotBlank
    @ValidEmail
    private String email;

    @NotBlank
    private boolean admin;

}
