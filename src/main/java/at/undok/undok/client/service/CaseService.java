package at.undok.undok.client.service;

import at.undok.undok.client.mapper.inter.CaseMapper;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.repository.CaseRepo;
import at.undok.undok.client.repository.CounselingRepo;
import at.undok.undok.client.util.StatusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepo caseRepo;
    private final ModelMapper modelMapper;
    private final CaseMapper caseMapper;
    private final CounselingRepo counselingRepo;

    public CaseDto createCase(CaseDto caseDto) {
        Case entity = caseMapper.toEntity(caseDto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStartDate(LocalDate.now());
        Case savedCase = caseRepo.save(entity);
        return caseMapper.toDto(savedCase);
    }

    public CaseDto getLastClosedCase(UUID clientId) {
        return caseMapper.toDto(caseRepo.findFirstByClientIdOrderByEndDateAsc(clientId));
    }

    public CaseDto getCaseById(UUID id) {
        return modelMapper.map(caseRepo.findById(id), CaseDto.class);
    }

    public CaseDto updateStatus(CaseDto caseDto) {
        Case aCase = caseRepo.findById(caseDto.getId()).orElseThrow();
        aCase.setStatus(caseDto.getStatus());
        aCase.setReferredTo(caseDto.getReferredTo());
        aCase.setUpdatedAt(LocalDateTime.now());
        aCase.setTotalConsultationTime(Objects.equals(caseDto.getStatus(), "CLOSED") ? counselingRepo.selectTotalConsultationTime(aCase.getId()) : null);
        aCase.setEndDate(Objects.equals(caseDto.getStatus(), "CLOSED") ? LocalDate.now() : null);
        return caseMapper.toDto(caseRepo.save(aCase));
    }

    public List<CaseDto> getCaseByClientIdAndStatus(UUID clientId, String status) {
        List<Case> caseList = caseRepo.findByClientIdAndStatus(clientId, status);
        return caseList.stream().map(caseMapper::toDto).toList();
    }

    public Boolean countOpenCases(UUID clientId) {
        Integer countCase = caseRepo.countOpenCases(clientId);
        if (countCase == 1) {
            return true;
        } else if (countCase >= 1) {
            return null;
        } else {
            return false;
        }
    }

    public Case createCase(UUID clientId, String keyword, String targetGroup) {
        Case c = new Case();
        c.setCreatedAt(LocalDateTime.now());
        c.setStartDate(LocalDate.now());
        c.setStatus(StatusService.STATUS_OPEN);
        String caseName = keyword + " - " + LocalDate.now();
        c.setName(caseName);
        c.setClientId(clientId);
        c.setTargetGroup(targetGroup);
        return caseRepo.save(c);
    }

    public CaseDto getCaseByClientId(UUID clientId) {
        UUID caseId = counselingRepo.findCaseId(clientId);
        return getCaseById(caseId);
    }

}
