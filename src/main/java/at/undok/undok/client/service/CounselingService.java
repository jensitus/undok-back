package at.undok.undok.client.service;

import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.form.CounselingForm;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CounselingService {

    @Autowired
    private CounselingRepo counselingRepo;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ToLocalDateService toLocalDateService;

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    public CounselingDto createCounseling(UUID clientId, CounselingForm counselingForm) {
        Counseling c = new Counseling();
        c.setCounselingStatus(counselingForm.getCounselingStatus());
        c.setActivity(counselingForm.getActivity());
        c.setConcern(counselingForm.getConcern());
        c.setConcernCategory(counselingForm.getConcernCategory());
        c.setCounselingDate(toLocalDateService.formatStringToLocalDateTime(counselingForm.getCounselingDate()));
        c.setActivityCategory(counselingForm.getActivityCategory());
        c.setRegisteredBy(counselingForm.getRegisteredBy());
        c.setCreatedAt(LocalDateTime.now());
        c.setComment(counselingForm.getComment());
        Optional<Client> clientOptional = clientRepo.findById(counselingForm.getClientId());
        c.setClient(clientOptional.get());
        Counseling counsel = counselingRepo.save(c);
        CounselingDto counselingDto = modelMapper.map(counsel, CounselingDto.class);
        return counselingDto;
    }

    public Long numberOfCounselings() {
        return counselingRepo.count();
    }

    public List<CounselingDto> getFutureCounselings() {
        List<Counseling> allInFuture = counselingRepo.findAllInFuture(LocalDateTime.now());
        return entityToDtoMapper.convertCounselingListToDtoList(allInFuture);
    }

    public List<CounselingDto> getPastCounselings() {
        List<Counseling> allInPast = counselingRepo.findAllInPast(LocalDateTime.now());
        return entityToDtoMapper.convertCounselingListToDtoList(allInPast);
    }

    public List<CounselingDto> getAllCounselings() {
        List<Counseling> all = counselingRepo.findAll();
        return entityToDtoMapper.convertCounselingListToDtoList(all);
    }

    public CounselingDto updateCounseling(CounselingDto counselingDto) {
        Counseling counseling = counselingRepo.getOne(counselingDto.getId());
        counseling.setCounselingDate(counselingDto.getCounselingDate());
        counseling.setCounselingStatus(counselingDto.getCounselingStatus());
        counseling.setConcern(counselingDto.getConcern());
        counseling.setCreatedAt(counselingDto.getCreatedAt());
        counseling.setActivity(counselingDto.getActivity());
        counseling.setConcernCategory(counselingDto.getConcernCategory());
        counseling.setActivityCategory(counselingDto.getActivityCategory());
        counseling.setComment(counselingDto.getComment());

        Counseling savedCounseling = counselingRepo.save(counseling);
        return modelMapper.map(savedCounseling, CounselingDto.class);
    }

    public CounselingDto setCommentOnCounseling(UUID counselingId, String comment) {
        Counseling counseling = counselingRepo.findById(counselingId).get();
        counseling.setComment(comment);
        Counseling savedCounseling = counselingRepo.save(counseling);
        return modelMapper.map(savedCounseling, CounselingDto.class);
    }

}
