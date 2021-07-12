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
        Optional<Client> clientOptional = clientRepo.findById(counselingForm.getClientId());
        c.setClient(clientOptional.get());
        Counseling counsel = counselingRepo.save(c);
        CounselingDto counselingDto = modelMapper.map(counsel, CounselingDto.class);
        return counselingDto;
    }

    public Long numberOfCounselings() {
        return counselingRepo.count();
    }

}
