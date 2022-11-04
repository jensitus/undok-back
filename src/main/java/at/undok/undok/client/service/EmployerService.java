package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.dto.CheckClientEmployerDto;
import at.undok.undok.client.model.dto.ClientEmployerJobDescriptionDto;
import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.entity.Address;
import at.undok.undok.client.model.entity.ClientEmployer;
import at.undok.undok.client.model.entity.Employer;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.form.EmployerForm;
import at.undok.undok.client.repository.AddressRepo;
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
    private final AddressRepo addressRepo;
    private final ModelMapper modelMapper;
    private final EntityToDtoMapper entityToDtoMapper;
    private final AttributeEncryptor attributeEncryptor;
    private final ClientEmployerService clientEmployerService;

    public EmployerDto setEmployer(EmployerForm employerForm) {
        Person employerPerson = new Person();

        Address address = new Address();
        if (employerForm.getEmployerCity() != null) {
            address.setCity(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerCity()));
        }
        if (employerForm.getEmployerZipCode() != null) {
            address.setZipCode(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerZipCode()));
        }
        if (employerForm.getEmployerStreet() != null) {
            address.setStreet(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerStreet()));
        }
        if (employerForm.getEmployerCountry() != null) {
            address.setCountry(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerCountry()));
        }
        Address savedAddress = addressRepo.save(address);

        if (employerForm.getEmployerFirstName() != null) {
            employerPerson.setFirstName(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerFirstName()));
        }
        if (employerForm.getEmployerLastName() != null) {
            employerPerson.setLastName(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerLastName()));
        }
        if (employerForm.getEmployerEmail() != null) {
            employerPerson.setEmail(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerEmail()));
        }
        if (employerForm.getEmployerTelephone() != null) {
            employerPerson.setTelephone(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerTelephone()));
        }
        employerPerson.setCreatedAt(LocalDateTime.now());

        Employer employer = new Employer();
        employer.setCompany(employerForm.getEmployerCompany());
        employer.setPosition(employerForm.getEmployerPosition());
        employer.setStatus(StatusService.STATUS_ACTIVE);
        employer.setCreatedAt(LocalDateTime.now());
        employer.setPerson(employerPerson);
        employer.getPerson().setAddress(savedAddress);
        Employer eWithAddress = employerRepo.save(employer);
        return modelMapper.map(eWithAddress, EmployerDto.class);
    }

    public EmployerDto getEmployerById(UUID id) {
        Employer employer = employerRepo.getById(id);
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
            Employer employer = employerRepo.getOne(ce.getEmployerId());
            EmployerDto employerDto = entityToDtoMapper.mapEmployerToDto(employer);
            ClientEmployerJobDescriptionDto clientEmployerJobDescriptionDto = new ClientEmployerJobDescriptionDto();
            clientEmployerJobDescriptionDto.setEmployer(employerDto);
            clientEmployerJobDescriptionDto.setFrom(ce.getFrom());
            clientEmployerJobDescriptionDto.setUntil(ce.getUntil());
            clientEmployerJobDescriptionDto.setIndustry(ce.getIndustry());
            clientEmployerJobDescriptionDto.setJobFunction(ce.getJobFunction());
            clientEmployerJobDescriptionDto.setIndustry(ce.getIndustry());
            clientEmployerJobDescriptionDto.setIndustrySub(ce.getIndustrySub());
            clientEmployerJobDescriptionDto.setJobRemarks(ce.getJobRemarks());
            clientEmployerJobDescriptionDtos.add(clientEmployerJobDescriptionDto);
        }
        return clientEmployerJobDescriptionDtos;
    }

    public Long getNumberOfEmployers() {
        return employerRepo.countByStatus(StatusService.STATUS_ACTIVE);
    }

    public EmployerDto updateEmployer(EmployerDto employerDto) {

        Address address = addressRepo.save(entityToDtoMapper.mapToAddress(employerDto.getPerson().getAddress()));
        Employer employer = entityToDtoMapper.mapDtoToEmployer(employerDto);
        employer.getPerson().setAddress(address);
        employer.setId(employerDto.getId());
        employerRepo.save(employer);
        return modelMapper.map(employer, EmployerDto.class);
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

}
