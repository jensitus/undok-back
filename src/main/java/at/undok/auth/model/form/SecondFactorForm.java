package at.undok.auth.model.form;

import lombok.Data;

import java.util.UUID;

@Data
public class SecondFactorForm {

    private UUID userId;
    private String token;

}
