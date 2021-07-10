package at.undok.undok.client.model.form;

import lombok.Data;

import java.util.UUID;

@Data
public class CounselingForm {

    private String counselingStatus;

    private String entryDate;

    private String concern;

    private String concernCategory;

    private String activity;

    private String activityCategory;

    private String registeredBy;

    private UUID clientId;

}
