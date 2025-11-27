package at.undok.undok.client.controller;

import at.undok.undok.client.api.ReportApi;
import at.undok.undok.client.model.dto.*;
import at.undok.undok.client.service.CounselingService;
import at.undok.undok.client.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController implements ReportApi {

    private final CounselingService counselingService;
    private final ReportService reportService;

    @Override
    public Long getNumberOfCounselingsByDateRange(LocalDateTime from, LocalDateTime to) {
        return reportService.numberOfCounselingsByDateRange(from, to);
    }

    @Override
    public Long getNumberOfClientsWithFirstCounselingInDateRange(LocalDateTime from, LocalDateTime to) {
        return reportService.numberOfClientsWithFirstCounselingInDateRange(from, to);
    }

    @Override
    public List<LanguageCount> countByLanguageInDateRange(LocalDateTime from, LocalDateTime to) {
        return reportService.countByLanguageInDateRange(from, to);
    }

    @Override
    public ResponseEntity<List<NationalityCount>> getNationalityCountsByDateRange(LocalDateTime from, LocalDateTime to) {
        return ResponseEntity.ok(reportService.getNationalityCountsByDateRange(from, to));
    }

    @Override
    public ResponseEntity<List<GenderCount>> getGenderCountsByDateRange(LocalDateTime from, LocalDateTime to) {
        return ResponseEntity.ok(reportService.getGenderCountsByDateRange(from, to));
    }

    @Override
    public ResponseEntity<List<SectorCount>> getSectorCountsByDateRange(LocalDateTime from, LocalDateTime to) {
        return ResponseEntity.ok(reportService.getSectorCountsByDateRange(from, to));
    }

    @Override
    public ResponseEntity<List<CounselingActivityCount>> getCounselingActivityCount(LocalDateTime from, LocalDateTime to) {
        return ResponseEntity.ok(reportService.getCounselingActivityCountsByDateRange(from, to));
    }

}
