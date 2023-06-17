package at.undok.undok.client.controller;

import at.undok.undok.client.api.CounselingApi;
import at.undok.undok.client.model.dto.AllCounselingDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.service.CounselingService;
import at.undok.undok.client.service.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CounselingController implements CounselingApi {

    private final CounselingService counselingService;
    private final CsvService csvService;

    @Override
    public Long getNumberOfCounselings() {
        return counselingService.numberOfCounselings();
    }

    @Override
    public List<CounselingDto> getOldCounselings() {
        return counselingService.getPastCounselings();
    }

    @Override
    public ResponseEntity<List<AllCounselingDto>> getAllCounselings() {
        return ResponseEntity.ok(counselingService.getAllCounselings());
    }

    @Override
    public CounselingDto updateCounseling(UUID counselingId, CounselingDto counselingDto) {
        return counselingService.updateCounseling(counselingDto);
    }

    @Override
    public CounselingDto setCommentOnCounseling(UUID counselingId, String comment) {
        return counselingService.setCommentOnCounseling(counselingId, comment);
    }

    @Override
    public void deleteCounseling(UUID counselingId) {
        counselingService.deleteCounseling(counselingId);
    }

    @Override
    public CounselingDto getSingleCounseling(UUID counselingId) {
        return counselingService.getSingleCounseling(counselingId);
    }

    @Override
    public ResponseEntity<Resource> getFile() {
        String filename = "counselings.csv";
        InputStreamResource file = new InputStreamResource(csvService.load());
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                             .contentType(MediaType.parseMediaType("application/csv"))
                             .body(file);
    }
}
