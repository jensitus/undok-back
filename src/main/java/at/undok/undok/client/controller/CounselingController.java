package at.undok.undok.client.controller;

import at.undok.undok.client.api.CounselingApi;
import at.undok.undok.client.model.dto.AllCounselingDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.service.CounselingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CounselingController implements CounselingApi {

    private final CounselingService counselingService;

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
}
