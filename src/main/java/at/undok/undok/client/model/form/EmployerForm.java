package at.undok.undok.client.model.form;

import lombok.Data;

@Data
public class EmployerForm {
    private String employerFirstName;
    private String employerLastName;
    private String employerDateOfBirth;
    private String employerCompany;
    private String employerPosition;
}