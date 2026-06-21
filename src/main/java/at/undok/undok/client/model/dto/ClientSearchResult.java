package at.undok.undok.client.model.dto;

import at.undok.undok.client.model.entity.Client;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class ClientSearchResult {
    private UUID id;
    private String keyword;
    private String lastName;
    private String firstName;
    private String comment;
    private String type = "client";
    private List<MatchedCategoryResult> matchedCategories = new ArrayList<>();

    public ClientSearchResult() {
    }

    public ClientSearchResult(Client client) {
        this.id = client.getId();
        this.keyword = client.getKeyword();
        this.lastName = client.getLastName();
        this.firstName = client.getFirstName();
        this.comment = client.getComment();
    }

    public ClientSearchResult(Client client, List<MatchedCategoryResult> matchedCategories) {
        this(client);
        this.matchedCategories = matchedCategories != null ? matchedCategories : new ArrayList<>();
    }

}
