package at.undok.auth.model.form;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SecondFactorForm {

    private UUID userId;
    private String token;

}
