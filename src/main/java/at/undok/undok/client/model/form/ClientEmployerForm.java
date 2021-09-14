package at.undok.undok.client.model.form;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ClientEmployerForm {

    private UUID employerId;
    private UUID clientId;
//    private LocalDate from;
//    private LocalDate until;

}
