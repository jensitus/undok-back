package at.undok.undok.client.model.form;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString
public class ClientEmployerForm {
    private UUID id;
    private UUID employerId;
    private UUID clientId;
    private LocalDate from;
    private LocalDate until;
    private String industry;
    private String industrySub;
    private String jobFunction;
    private String jobRemarks;

}
