package org.service.b.auth.model.form;

import lombok.Data;
import lombok.ToString;
import org.service.b.auth.validation.annotation.ValidEmail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
