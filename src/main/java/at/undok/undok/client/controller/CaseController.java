package at.undok.undok.client.controller;

import at.undok.undok.client.api.CaseApi;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.service.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CaseController implements CaseApi {

    private final CaseService caseService;

    @Override
    public ResponseEntity<CaseDto> getCase(UUID id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    @Override
    public ResponseEntity<CaseDto> createCase(CaseDto caseDto) {
        return ResponseEntity.ok(caseService.createCase(caseDto));
    }

    @Override
    public ResponseEntity<CaseDto> updateCase(CaseDto caseDto, String id) {
        return ResponseEntity.ok(caseService.updateStatus(caseDto));
    }

}
