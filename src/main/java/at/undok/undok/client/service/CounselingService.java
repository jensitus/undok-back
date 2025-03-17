package at.undok.undok.client.service;

import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.exception.CounselingDateException;
import at.undok.undok.client.model.dto.AllCounselingDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.dto.CounselingForCsvResult;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.form.CounselingForm;
import at.undok.undok.client.repository.CaseRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import at.undok.undok.client.repository.JoinCategoryRepo;
import at.undok.undok.client.util.StatusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
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
    private final JoinCategoryRepo joinCategoryRepo;
    private final CaseRepo caseRepo;

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
        setCase(counseling, clientId);
        return modelMapper.map(counseling, CounselingDto.class);
    }

    private void setCase(Counseling c, UUID clientId) {
        if (Boolean.FALSE.equals(countCase(clientId))) {
            Case aCase = createCase(c);
            c.setCounselingCase(aCase);
            counselingRepo.save(c);
        } else if (Boolean.TRUE.equals(countCase(clientId))) {
            c.setCounselingCase(getCase(clientId));
            counselingRepo.save(c);
        } else if (countCase(clientId) == null) {
            throw new RuntimeException("too much cases");
        }
    }

    private Boolean countCase(UUID clientId) {
        Integer countCase = counselingRepo.countCase(clientId);
        if (countCase == 1) {
            return true;
        } else if (countCase >= 1) {
            return null;
        } else {
            return false;
        }
    }

    private Case createCase(Counseling counseling) {
        Case c = new Case();
        UUID clientId = counseling.getClient().getId();
        c.setCreatedAt(LocalDateTime.now());
        c.setStartDate(LocalDate.now());
        c.setStatus("OPEN");
        Client client = counseling.getClient();
        String keyword = client.getKeyword();
        String caseName = keyword + " - " + LocalDate.now();
        c.setName(caseName);
        c.setClientId(clientId);
        return caseRepo.save(c);
    }

    private Case getCase(UUID clientId) {
        UUID caseId = counselingRepo.findCaseId(clientId);
        return caseRepo.findById(caseId).orElseThrow();
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

    public int getTotalConsultationTime(UUID caseId) {
        return counselingRepo.selectTotalConsultationTime(caseId);
    }

    public List<CounselingDto> findByClient(UUID clientId) {
        List<Counseling> counselings = counselingRepo.findByClientId(clientId);
        return entityToDtoMapper.convertCounselingListToDtoList(counselings);
    }

}
