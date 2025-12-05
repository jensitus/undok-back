package at.undok.undok.client.model.dto;

import at.undok.undok.client.mapper.inter.CaseMapper;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CounselingDto {

    private UUID id;
    private String counselingStatus;
    private LocalDate entryDate;
    private String concern;
    private String activity;
    private String registeredBy;
    private String counselingDate;
    private LocalDateTime createdAt;
    private String comment;
    private UUID clientId;
    private String clientFullName;
    private String keyword;
    private Integer requiredTime;
    private CaseDto counselingCase;

    public CounselingDto(UUID id, String concern, String activity, String counselingDate, String comment, UUID clientId, String keyword) {
        this.id = id;
        this.concern = concern;
        this.activity = activity;
        this.counselingDate = counselingDate;
        this.comment = comment;
        this.clientId = clientId;
        this.keyword = keyword;
    }

    public CounselingDto(UUID id, String concern, String activity, String registeredBy, String counselingDate,
                         LocalDateTime createdAt, String comment, UUID clientId, String clientFullName,
                         String keyword, Integer requiredTime) {
        this.id = id;
        this.concern = concern;
        this.activity = activity;
        this.registeredBy = registeredBy;
        this.counselingDate = counselingDate;
        this.createdAt = createdAt;
        this.comment = comment;
        this.clientId = clientId;
        this.clientFullName = clientFullName;
        this.keyword = keyword;
        this.requiredTime = requiredTime;
    }

    public static CounselingDto from(Counseling counseling) {
        CounselingDto dto = new CounselingDto();
        dto.setId(counseling.getId());
        dto.setCounselingStatus(counseling.getCounselingStatus());
        dto.setEntryDate(counseling.getEntryDate());
        dto.setConcern(counseling.getConcern());
        dto.setActivity(counseling.getActivity());
        dto.setRegisteredBy(counseling.getRegisteredBy());
        dto.setCounselingDate(counseling.getCounselingDate().toString());
        dto.setCreatedAt(counseling.getCreatedAt());
        dto.setComment(counseling.getComment());
        dto.setRequiredTime(counseling.getRequiredTime());
        dto.setKeyword(counseling.getClient().getKeyword());

        // Map client info if available
        if (counseling.getClient() != null) {
            Client client = counseling.getClient();
            // Person person = client.getPerson();
            String firstName = client.getFirstName() != null ? client.getFirstName() : null;
            String lastName = client.getLastName() != null ? client.getLastName() : null;
            dto.setClientId(counseling.getClient().getId());
            dto.setClientFullName(firstName + " " + lastName);
        }

        return dto;
    }

}
