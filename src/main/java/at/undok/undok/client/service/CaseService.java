package at.undok.undok.client.service;

import at.undok.undok.client.exception.CaseAlreadyClosedException;
import at.undok.undok.client.exception.InvalidCaseEndDateException;
import at.undok.undok.client.exception.TooMuchCasesException;
import at.undok.undok.client.mapper.inter.CaseMapper;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.dto.ClientCaseProjection;
import at.undok.undok.client.model.form.CloseCaseForm;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseService {

    private static final String OPEN = "OPEN";

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

    /**
     * Closes a case: stamps the end date the counsellor picked, records who the client was
     * referred on to, and freezes the total consultation time summed over the case's counselings.
     */
    public CaseDto closeCase(UUID caseId, CloseCaseForm form) {
        Case aCase = caseRepo.findById(caseId).orElseThrow();
        if (StatusService.STATUS_CLOSED.equals(aCase.getStatus())) {
            throw new CaseAlreadyClosedException("Dieser Fall ist bereits abgeschlossen.");
        }
        LocalDate endDate = form.getEndDate() != null ? form.getEndDate() : LocalDate.now();
        if (endDate.isAfter(LocalDate.now())) {
            throw new InvalidCaseEndDateException("Das Enddatum darf nicht in der Zukunft liegen.");
        }
        if (aCase.getStartDate() != null && endDate.isBefore(aCase.getStartDate())) {
            throw new InvalidCaseEndDateException("Das Enddatum darf nicht vor dem Startdatum liegen.");
        }
        aCase.setStatus(StatusService.STATUS_CLOSED);
        aCase.setEndDate(endDate);
        aCase.setReferredTo(form.getReferredTo());
        aCase.setTotalConsultationTime(counselingRepo.selectTotalConsultationTime(aCase.getId()));
        aCase.setUpdatedAt(LocalDateTime.now());
        return caseMapper.toDto(caseRepo.save(aCase));
    }

    /**
     * Batch-loads the case that currently represents each client, keyed by client id, so the
     * clients list can be split into open and closed without an N+1 per client.
     *
     * @return clients with no case at all are simply absent from the map
     */
    public Map<UUID, ClientCaseProjection> getCurrentCaseByClient(List<UUID> clientIds) {
        if (clientIds == null || clientIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return caseRepo.findCurrentCaseForClients(clientIds)
                       .stream()
                       .collect(Collectors.toMap(ClientCaseProjection::getClientId, projection -> projection));
    }

    /**
     * Updates the client's OPEN case from the client form.
     *
     * @return null when the client has no open case — e.g. every case is closed, or the client
     *         has none at all. Callers must guard: there is nothing to hang case-scoped data off.
     */
    public CaseDto updateCase(UUID clientId,
                              String workingRelationship,
                              Boolean humanTrafficking,
                              Boolean jobCenterBlock,
                              String targetGroup) {
        List<Case> caseList = caseRepo.findByClientIdAndStatus(clientId, OPEN);
        if (caseList.size() > 1) {
            throw new TooMuchCasesException("too many open cases");
        } else if (caseList.size() == 1) {
            Case aCase = caseList.get(0);
            aCase.setWorkingRelationship(workingRelationship);
            aCase.setHumanTrafficking(humanTrafficking);
            aCase.setJobCenterBlock(jobCenterBlock);
            aCase.setTargetGroup(targetGroup);
            Case saved = caseRepo.save(aCase);
            return caseMapper.toDto(saved);
        }
        return null;
    }

    /**
     * @return oldest first, so the last element is the most recently closed case — which is the
     *         one the client detail page and the edit form fall back to once nothing is open.
     */
    public List<CaseDto> getCaseByClientIdAndStatus(UUID clientId, String status) {
        List<Case> caseList = caseRepo.findByClientIdAndStatusOrderByEndDateAsc(clientId, status);
        List<CaseDto> caseDtoList = caseList.stream().map(caseMapper::toDto).toList();
        for (CaseDto caseDto : caseDtoList) {
            caseDto.setCounselingLanguages(categoryService.getCategoryListByTypeAndEntity(CategoryType.COUNSELING_LANGUAGE, caseDto.getId()));
            caseDto.setJobMarketAccess(categoryService.getCategoryListByTypeAndEntity(CategoryType.JOB_MARKET_ACCESS, caseDto.getId()));
            caseDto.setOriginOfAttention(categoryService.getCategoryListByTypeAndEntity(CategoryType.ORIGIN_OF_ATTENTION, caseDto.getId()));
            caseDto.setUndocumentedWork(categoryService.getCategoryListByTypeAndEntity(CategoryType.UNDOCUMENTED_WORK, caseDto.getId()));
            caseDto.setComplaints(categoryService.getCategoryListByTypeAndEntity(CategoryType.COMPLAINT, caseDto.getId()));
            caseDto.setIndustryUnion(categoryService.getCategoryListByTypeAndEntity(CategoryType.INDUSTRY_UNION, caseDto.getId()));
            caseDto.setJobFunction(categoryService.getCategoryListByTypeAndEntity(CategoryType.JOB_FUNCTION, caseDto.getId()));
            caseDto.setSector(categoryService.getCategoryListByTypeAndEntity(CategoryType.SECTOR, caseDto.getId()));
            caseDto.setResidenceStatus(categoryService.getCategoryListByTypeAndEntity(CategoryType.AUFENTHALTSTITEL, caseDto.getId()));
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
