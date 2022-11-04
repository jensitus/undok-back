package at.undok.undok.client.model.form;

import at.undok.undok.client.model.dto.CategoryDto;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CounselingForm {

    private String counselingStatus;

    private String entryDate;

    private String concern;

    private String concernCategory;

    private String activity;

    private Set<CategoryDto> activityCategory;

    private String registeredBy;

    private UUID clientId;

    private String counselingDate;

    private String comment;

}
