package at.undok.undok.client.service;

import at.undok.undok.client.mapper.inter.CaseMapper;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.repository.CaseRepo;
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
    private final CounselingService counselingService;

    public CaseDto createCase(CaseDto caseDto) {
        Case entity = caseMapper.toEntity(caseDto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStartDate(LocalDate.now());
        Case savedCase = caseRepo.save(entity);
        return caseMapper.toDto(savedCase);
    }

    public CaseDto getCase(UUID id) {
        return modelMapper.map(caseRepo.findById(id), CaseDto.class);
    }

    public CaseDto updateStatus(CaseDto caseDto) {
        Case aCase = caseRepo.findById(caseDto.getId()).orElseThrow();
        aCase.setStatus(caseDto.getStatus());
        aCase.setReferredTo(caseDto.getReferredTo());
        aCase.setUpdatedAt(LocalDateTime.now());
        aCase.setTotalConsultationTime(Objects.equals(caseDto.getStatus(), "CLOSED") ? counselingService.getTotalConsultationTime(aCase.getId()) : null);
        return caseMapper.toDto(caseRepo.save(aCase));
    }

    public List<CaseDto> getCaseByClientIdAndStatus(UUID clientId, String status) {
        List<Case> caseList = caseRepo.findByClientIdAndStatus(clientId, status);
        return caseList.stream().map(caseMapper::toDto).toList();
    }

}
