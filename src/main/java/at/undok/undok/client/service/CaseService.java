package at.undok.undok.client.service;

import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.repository.CaseRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepo caseRepo;
    private final ModelMapper modelMapper;

    public CaseDto createCase(CaseDto caseDto) {
        return modelMapper.map(caseRepo.save(modelMapper.map(caseDto, Case.class)), CaseDto.class);
    }

    public CaseDto getCase(UUID id) {
        return modelMapper.map(caseRepo.findById(id), CaseDto.class);
    }

    public CaseDto updateStatus(CaseDto caseDto) {
        Case aCase = caseRepo.findById(caseDto.getId()).orElseThrow();
        aCase.setStatus(caseDto.getStatus());
        aCase.setReferredTo(caseDto.getReferredTo());
        return modelMapper.map(caseRepo.save(aCase), CaseDto.class);
    }

}
