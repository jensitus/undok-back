package at.undok.undok.client.controller;

import at.undok.undok.client.api.ReportApi;
import at.undok.undok.client.model.dto.LanguageCount;
import at.undok.undok.client.model.dto.NationalityCount;
import at.undok.undok.client.service.CounselingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController implements ReportApi {

    private final CounselingService counselingService;

    @Override
    public Long getNumberOfCounselingsByDateRange(LocalDateTime from, LocalDateTime to) {
        return counselingService.numberOfCounselingsByDateRange(from, to);
    }

    @Override
    public Long getNumberOfClientsWithFirstCounselingInDateRange(LocalDateTime from, LocalDateTime to) {
        return counselingService.numberOfClientsWithFirstCounselingInDateRange(from, to);
    }

    @Override
    public List<LanguageCount> countByLanguageInDateRange(LocalDateTime from, LocalDateTime to) {
        return counselingService.countByLanguageInDateRange(from, to);
    }

    @Override
    public ResponseEntity<List<NationalityCount>> getNationalityCountsByDateRange(LocalDateTime from, LocalDateTime to) {
        return ResponseEntity.ok(counselingService.getNationalityCountsByDateRange(from, to));
    }
}
