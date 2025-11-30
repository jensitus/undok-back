package at.undok.undok.client.service;

import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.exception.CounselingDateException;
import at.undok.undok.client.exception.TooMuchCasesException;
import at.undok.undok.client.mapper.inter.CaseMapper;
import at.undok.undok.client.mapper.inter.CounselingMapper;
import at.undok.undok.client.model.dto.AllCounselingDto;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.dto.CounselingForCsvResult;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.form.CounselingForm;
import at.undok.undok.client.repository.CaseRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import at.undok.undok.client.util.StatusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CounselingService {

    private final CounselingRepo counselingRepo;
    private final ClientRepo clientRepo;
    private final ModelMapper modelMapper;
    private final ToLocalDateService toLocalDateService;
    private final EntityToDtoMapper entityToDtoMapper;
    private final CounselingMapper counselingMapper;
    private final CaseMapper caseMapper;
    private final CaseRepo caseRepo;
    private final CaseService caseService;

    public CounselingDto createCounseling(UUID clientId, CounselingForm counselingForm) {
        Counseling c = new Counseling();
        c.setCounselingStatus(counselingForm.getCounselingStatus());
        c.setActivity(counselingForm.getActivity());
        c.setConcern(counselingForm.getConcern());
        c.setConcernCategory(counselingForm.getConcernCategory());
        c.setRegisteredBy(counselingForm.getRegisteredBy());
        c.setCreatedAt(LocalDateTime.now());
        c.setComment(counselingForm.getComment());
        c.setStatus(StatusService.STATUS_ACTIVE);
        Client client = clientRepo.findById(counselingForm.getClientId()).orElseThrow();

        c.setClient(client);
        Counseling counseling = counselingRepo.save(c);
        Case aCase = setCase(counseling, clientId, client.getKeyword());
        CounselingDto counselingDto = counselingMapper.toDto(counseling);
        CaseDto caseDto = caseMapper.toDto(aCase);
        counselingDto.setCounselingCase(caseDto);
        return counselingDto;

    }

    private Case setCase(Counseling counseling, UUID clientId, String keyword) {
        if (Boolean.FALSE.equals(caseService.countOpenCases(clientId))) {
            Case aCase = caseRepo.findFirstByClientIdOrderByEndDateAsc(clientId);
            if (aCase != null) {
                // reopen case:
                aCase.setEndDate(null);
                aCase.setStatus(StatusService.STATUS_OPEN);
            } else {
                aCase = caseService.createCase(clientId, keyword, null, null, null, null);
            }
            // Case savedCase = caseRepo.save(aCase);
            counseling.setCounselingCase(aCase);
            counselingRepo.save(counseling);
            return aCase;
        } else if (Boolean.TRUE.equals(caseService.countOpenCases(clientId))) {
//            CaseDto currentCase = caseService.getCaseDtoByClientId(clientId);
            Case aCase = caseService.getCaseByClientId(clientId);
            counseling.setCounselingCase(aCase);
            counselingRepo.save(counseling);
            return aCase;
        } else if (caseService.countOpenCases(clientId) == null) {
            throw new TooMuchCasesException("there are too much open cases, please contact your administrator");
        } else {
            return null;
        }
    }


    public Long numberOfCounselings() {
        return counselingRepo.countByStatus(StatusService.STATUS_ACTIVE);
    }

    public List<CounselingDto> getFutureCounselings() {
        List<Counseling> allInFuture = counselingRepo.findAllInFuture(LocalDateTime.now());
        return entityToDtoMapper.convertCounselingListToDtoList(allInFuture);
    }

    public List<CounselingDto> getPastCounselings() {
        List<Counseling> allInPast = counselingRepo.findAllInPast(LocalDateTime.now());
        return entityToDtoMapper.convertCounselingListToDtoList(allInPast);
    }

    public List<AllCounselingDto> getAllCounselings() {
        List<Counseling> all = counselingRepo.findByStatusOrderByCounselingDateDesc(StatusService.STATUS_ACTIVE);
        return entityToDtoMapper.convertCounselingListToDtoForTableList(all);
    }

    public void setStatusDeleted(List<UUID> counselingIdList) {
        counselingIdList.forEach(id -> {
            Optional<Counseling> counselingOptional = counselingRepo.findById(id);
            if (counselingOptional.isPresent()) {
                Counseling counseling = counselingOptional.get();
                counseling.setStatus(StatusService.STATUS_DELETED);
                counselingRepo.save(counseling);
            }
        });
    }

    public CounselingDto updateCounseling(CounselingDto counselingDto) {
        Counseling counseling = counselingRepo.findById(counselingDto.getId()).orElseThrow();
        try {
            counseling.setCounselingDate(LocalDateTime.parse(counselingDto.getCounselingDate()));
        } catch (NullPointerException | DateTimeParseException e) {
            if (e.getClass().equals(NullPointerException.class)) {
                throw new CounselingDateException("Counseling Date Required");
            } else {
                throw e;
            }
        }
        counseling.setCounselingStatus(counselingDto.getCounselingStatus());
        counseling.setConcern(counselingDto.getConcern());
        counseling.setCreatedAt(counselingDto.getCreatedAt());
        counseling.setActivity(counselingDto.getActivity());
        counseling.setComment(counselingDto.getComment());
        counseling.setUpdatedAt(LocalDateTime.now());

        Counseling savedCounseling = counselingRepo.save(counseling);
        return entityToDtoMapper.convertCounselingToDto(savedCounseling);
    }

    public CounselingDto setCommentOnCounseling(UUID counselingId, String comment) {
        Counseling counseling = counselingRepo.findById(counselingId).orElseThrow();
        if ("null".equals(comment)) {
            counseling.setComment(null);
        } else {
            counseling.setComment(comment);
        }
        Counseling savedCounseling = counselingRepo.save(counseling);
        return entityToDtoMapper.convertCounselingToDto(savedCounseling);
    }

    public void deleteCounseling(UUID counselingId) {
        counselingRepo.deleteById(counselingId);
    }

    public CounselingDto getSingleCounseling(UUID counselingId) {
        Optional<Counseling> counseling = counselingRepo.findById(counselingId);
        if (counseling.isPresent()) {
            return entityToDtoMapper.convertCounselingToDto(counseling.get());
        } else {
            throw new RuntimeException("counseling is not present, sorry");
        }
    }

    public List<Counseling> getCounselingsWithoutDate() {
        return counselingRepo.findByCounselingDateIsNull();
    }

    public List<CounselingForCsvResult> getCounselingsForCsv() {
        return counselingRepo.getCounselingForCsv();
    }

    public CounselingDto setRequiredTime(UUID counselingId, Integer requiredTime) {
        Counseling counseling = counselingRepo.findById(counselingId).orElseThrow();
        counseling.setRequiredTime(requiredTime);
        Counseling saved = counselingRepo.save(counseling);
        return entityToDtoMapper.convertCounselingToDto(saved);
    }

    public Integer getTotalConsultationTime(UUID caseId) {
        return counselingRepo.selectTotalConsultationTime(caseId);
    }

    public List<CounselingDto> findByClient(UUID clientId, String order) {
        List<Counseling> counselings;
        if (Objects.equals(order, "Desc")) {
            counselings = counselingRepo.findByClientIdOrderByCounselingDateDesc(clientId);
        } else {
            counselings = counselingRepo.findByClientIdOrderByCounselingDateAsc(clientId);
        }
        return entityToDtoMapper.convertCounselingListToDtoList(counselings);
    }

    public Page<CounselingDto> search(String searchTerm, int page, int size) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(page, size);
        return counselingRepo.fullTextSearch(searchTerm.trim(), pageable)
                             .map(CounselingDto::from);
    }

}
