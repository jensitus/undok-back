package at.undok.undok.client.controller;

import at.undok.undok.client.api.CounselingApi;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.service.CounselingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<CounselingDto> getAllCounselings() {
        return counselingService.getAllCounselings();
    }

    @Override
    public CounselingDto createAnonymousCounseling() {
        return null;
    }
}
