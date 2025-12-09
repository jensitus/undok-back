package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.dto.UnifiedSearchResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/service/undok")
@PreAuthorize("hasRole('USER')")
public interface SearchApi {

    /**
     * Unified paginated search endpoint
     * Searches across both counselings and clients with pagination
     */
    @GetMapping("/search")
    UnifiedSearchResponse search(@RequestParam String q,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate);

    @GetMapping("/counselings/search")
    List<CounselingDto> searchCounselings(@RequestParam String q);

    @GetMapping("/clients/search")
    List<ClientDto> searchClients(@RequestParam String q);

}
