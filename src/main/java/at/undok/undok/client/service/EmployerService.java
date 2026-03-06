package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.dto.CheckClientEmployerDto;
import at.undok.undok.client.model.dto.ClientEmployerJobDescriptionDto;
import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.entity.ClientEmployer;
import at.undok.undok.client.model.entity.Employer;
import at.undok.undok.client.model.form.EmployerForm;
import at.undok.undok.client.repository.EmployerRepo;
import at.undok.undok.client.util.StatusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepo employerRepo;
    private final EntityToDtoMapper entityToDtoMapper;
    private final ClientEmployerService clientEmployerService;

    public EmployerDto setEmployer(EmployerForm employerForm) {
        Employer employer = new Employer();
        employer.setCompany(employerForm.getEmployerCompany());
        employer.setPosition(employerForm.getEmployerPosition());
        employer.setStatus(StatusService.STATUS_ACTIVE);
        employer.setCreatedAt(LocalDateTime.now());

        // Set person fields directly
        if (employerForm.getEmployerFirstName() != null) {
            employer.setFirstName(employerForm.getEmployerFirstName());
        }
        if (employerForm.getEmployerLastName() != null) {
            employer.setLastName(employerForm.getEmployerLastName());
        }
        if (employerForm.getEmployerEmail() != null) {
            employer.setEmail(employerForm.getEmployerEmail());
        }
        if (employerForm.getEmployerTelephone() != null) {
            employer.setTelephone(employerForm.getEmployerTelephone());
        }

        // Set address fields directly
        if (employerForm.getEmployerCity() != null) {
            employer.setCity(employerForm.getEmployerCity());
        }
        if (employerForm.getEmployerZipCode() != null) {
            employer.setZipCode(employerForm.getEmployerZipCode());
        }
        if (employerForm.getEmployerStreet() != null) {
            employer.setStreet(employerForm.getEmployerStreet());
        }
        if (employerForm.getEmployerCountry() != null) {
            employer.setCountry(employerForm.getEmployerCountry());
        }

        Employer savedEmployer = employerRepo.save(employer);
        return entityToDtoMapper.mapEmployerToDto(savedEmployer);
    }

    public EmployerDto getEmployerById(UUID id) {
        Employer employer = employerRepo.findById(id).orElseThrow();
        return entityToDtoMapper.mapEmployerToDto(employer);
    }

    public List<EmployerDto> getEmployers(UUID clientId) {
        List<Employer> employers = employerRepo.findByStatusOrderByCreatedAtDesc(StatusService.STATUS_ACTIVE);
        List<EmployerDto> employerDtos = entityToDtoMapper.convertEmployerListToDto(employers);
        if (clientId != null) {
            List<EmployerDto> employersWhereClientIsNotEmployed = new ArrayList<>();
            for (EmployerDto e : employerDtos) {
                if (!clientEmployerService.checkClientEmployer(e.getId(), clientId)) {
                    employersWhereClientIsNotEmployed.add(e);
                }
            }
            return employersWhereClientIsNotEmployed;
        } else {
            for (EmployerDto e : employerDtos) {
                e.setClients(clientEmployerService.getClientsForEmployer(e.getId()));
            }
            return employerDtos;
        }
    }

    public List<ClientEmployerJobDescriptionDto> getByClientId(UUID clientId) {
        List<ClientEmployer> clientEmployers = clientEmployerService.getByClientId(clientId);
        List<ClientEmployerJobDescriptionDto> clientEmployerJobDescriptionDtos = new ArrayList<>();
        for (ClientEmployer ce : clientEmployers) {
            Employer employer = employerRepo.getReferenceById(ce.getEmployerId());
            EmployerDto employerDto = entityToDtoMapper.mapEmployerToDto(employer);
            ClientEmployerJobDescriptionDto clientEmployerJobDescriptionDto = getClientEmployerJobDescriptionDto(ce, employerDto);
            clientEmployerJobDescriptionDtos.add(clientEmployerJobDescriptionDto);
        }
        return clientEmployerJobDescriptionDtos;
    }

    public Long getNumberOfEmployers() {
        return employerRepo.countByStatus(StatusService.STATUS_ACTIVE);
    }

    public EmployerDto updateEmployer(EmployerDto employerDto) {
        Employer toBeUpdatedEmployer = employerRepo.findById(employerDto.getId()).orElseThrow();

        // Update employer fields
        toBeUpdatedEmployer.setPosition(employerDto.getPosition());
        toBeUpdatedEmployer.setCompany(employerDto.getCompany());
        toBeUpdatedEmployer.setUpdatedAt(LocalDateTime.now());

        // Update person fields directly
        toBeUpdatedEmployer.setFirstName(employerDto.getFirstName());
        toBeUpdatedEmployer.setLastName(employerDto.getLastName());
        toBeUpdatedEmployer.setEmail(employerDto.getEmail());
        toBeUpdatedEmployer.setTelephone(employerDto.getTelephone());
        toBeUpdatedEmployer.setGender(employerDto.getGender());
        toBeUpdatedEmployer.setDateOfBirth(employerDto.getDateOfBirth());
        toBeUpdatedEmployer.setContactData(employerDto.getContactData());

        // Update address fields directly
        toBeUpdatedEmployer.setStreet(employerDto.getStreet());
        toBeUpdatedEmployer.setCity(employerDto.getCity());
        toBeUpdatedEmployer.setCountry(employerDto.getCountry());
        toBeUpdatedEmployer.setZipCode(employerDto.getZipCode());

        Employer savedEmployer = employerRepo.save(toBeUpdatedEmployer);

        return entityToDtoMapper.mapEmployerToDto(savedEmployer);
    }

    public void setStatusDeleted(UUID employerId) {
        CheckClientEmployerDto checkActiveClient = employerRepo.checkActiveClient(employerId, StatusService.STATUS_ACTIVE);
        if (checkActiveClient.getCount() > 0) {
            throw new RuntimeException("There are still active clients related to this employer");
        }
        Optional<Employer> employerOptional = employerRepo.findById(employerId);
        if (employerOptional.isPresent()) {
            Employer employer = employerOptional.get();
            employer.setStatus(StatusService.STATUS_DELETED);
            employerRepo.save(employer);
        } else {
            throw new NoSuchElementException("No Employer found with ID " + employerId);
        }
    }

    private static ClientEmployerJobDescriptionDto getClientEmployerJobDescriptionDto(ClientEmployer ce, EmployerDto employerDto) {
        ClientEmployerJobDescriptionDto clientEmployerJobDescriptionDto = new ClientEmployerJobDescriptionDto();
        clientEmployerJobDescriptionDto.setId(ce.getId());
        clientEmployerJobDescriptionDto.setEmployer(employerDto);
        clientEmployerJobDescriptionDto.setFrom(ce.getFrom());
        clientEmployerJobDescriptionDto.setUntil(ce.getUntil());
        clientEmployerJobDescriptionDto.setIndustry(ce.getIndustry());
        clientEmployerJobDescriptionDto.setJobFunction(ce.getJobFunction());
        clientEmployerJobDescriptionDto.setIndustry(ce.getIndustry());
        clientEmployerJobDescriptionDto.setIndustrySub(ce.getIndustrySub());
        clientEmployerJobDescriptionDto.setJobRemarks(ce.getJobRemarks());
        return clientEmployerJobDescriptionDto;
    }

}
