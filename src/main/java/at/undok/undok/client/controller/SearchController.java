package at.undok.undok.client.controller;

import at.undok.undok.client.api.SearchApi;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.dto.UnifiedSearchResponse;
import at.undok.undok.client.service.SearchService;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class SearchController implements SearchApi {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public UnifiedSearchResponse search(String q, int page, int size, LocalDateTime startDate, LocalDateTime endDate) {
        return searchService.searchAll(q, startDate, endDate, page, size);
    }

    @Override
    public List<CounselingDto> searchCounselings(String q) {
        return searchService.searchCounselings(q);
    }

    @Override
    public List<ClientDto> searchClients(String q) {
        return searchService.searchClients(q);
    }
}
