package at.undok.undok.client.controller;

import at.undok.undok.client.api.CounselingApi;
import at.undok.undok.client.service.CounselingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CounselingController implements CounselingApi {

    @Autowired
    private CounselingService counselingService;

    @Override
    public Long getNumberOfCounselings() {
        return counselingService.numberOfCounselings();
    }
}
