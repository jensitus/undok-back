package at.undok.undok.client.model.dto;

import at.undok.undok.client.model.entity.Counseling;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CounselingSearchResult {

    // Getters and setters
    private UUID id;
    private String concern;
    private String activity;
    private String type = "counseling";
    private UUID clientId;
    private String keyword;

    public CounselingSearchResult() {
    }

    public CounselingSearchResult(Counseling counseling) {
        this.id = counseling.getId();
        this.concern = counseling.getConcern();
        this.activity = counseling.getActivity();
        this.clientId = counseling.getClient().getId();
        this.keyword = counseling.getClient().getKeyword();
    }

}
