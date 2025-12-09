package at.undok.undok.client.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UnifiedSearchResponse {

    // Getters and setters
    private List<CounselingSearchResult> counselings;
    private List<ClientSearchResult> clients;
    private int totalResults;
    private PaginationInfo pagination;

    public UnifiedSearchResponse() {
    }

    public UnifiedSearchResponse(List<CounselingSearchResult> counselings,
                                 List<ClientSearchResult> clients, PaginationInfo pagination) {
        this.counselings = counselings;
        this.clients = clients;
        this.totalResults = counselings.size() + clients.size();
        this.pagination = pagination;
    }

}
