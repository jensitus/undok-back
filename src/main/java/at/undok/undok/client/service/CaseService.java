package at.undok.undok.client.service;

import at.undok.undok.client.mapper.inter.CaseMapper;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.repository.CaseRepo;
import at.undok.undok.client.repository.CounselingRepo;
import at.undok.undok.client.util.CategoryType;
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
    private final CategoryService categoryService;

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
        Case aCase = caseRepo.findById(id).orElseThrow();
        return modelMapper.map(aCase, CaseDto.class);
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

    public CaseDto updateCase(UUID clientId,
                              String workingRelationship,
                              Boolean humanTrafficking,
                              Boolean jobCenterBlock,
                              String targetGroup) {
        Case aCase = caseRepo.findByClientId(clientId);
        aCase.setWorkingRelationship(workingRelationship);
        aCase.setHumanTrafficking(humanTrafficking);
        aCase.setJobCenterBlock(jobCenterBlock);
        aCase.setTargetGroup(targetGroup);
        Case saved = caseRepo.save(aCase);
        return caseMapper.toDto(saved);
    }

    public List<CaseDto> getCaseByClientIdAndStatus(UUID clientId, String status) {
        List<Case> caseList = caseRepo.findByClientIdAndStatus(clientId, status);
        List<CaseDto> caseDtoList = caseList.stream().map(caseMapper::toDto).toList();
        for (CaseDto caseDto : caseDtoList) {
            caseDto.setCounselingLanguages(categoryService.getCategoryListByTypeAndEntity(CategoryType.COUNSELING_LANGUAGE, caseDto.getId()));
            caseDto.setJobMarketAccess(categoryService.getCategoryListByTypeAndEntity(CategoryType.JOB_MARKET_ACCESS, caseDto.getId()));
            caseDto.setOriginOfAttention(categoryService.getCategoryListByTypeAndEntity(CategoryType.ORIGIN_OF_ATTENTION, caseDto.getId()));
            caseDto.setUndocumentedWork(categoryService.getCategoryListByTypeAndEntity(CategoryType.UNDOCUMENTED_WORK, caseDto.getId()));
        }
        return caseDtoList;
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

    public Case createCase(UUID clientId, String keyword, String targetGroup, Boolean humanTrafficking, Boolean jobCenterBlock, String workingRelationship) {
        Case c = new Case();
        c.setCreatedAt(LocalDateTime.now());
        c.setStartDate(LocalDate.now());
        c.setStatus(StatusService.STATUS_OPEN);
        String caseName = keyword + " - " + LocalDate.now();
        c.setName(caseName);
        c.setClientId(clientId);
        c.setTargetGroup(targetGroup);
        c.setHumanTrafficking(humanTrafficking);
        c.setJobCenterBlock(jobCenterBlock);
        c.setWorkingRelationship(workingRelationship);
        return caseRepo.save(c);
    }

    public CaseDto getCaseDtoByClientId(UUID clientId) {
        UUID caseId = counselingRepo.findCaseId(clientId);
        return getCaseById(caseId);
    }

    public Case getCaseByClientId(UUID clientId) {
        UUID caseId = counselingRepo.findCaseId(clientId);
        return caseRepo.findById(caseId).orElseThrow();
    }

}
