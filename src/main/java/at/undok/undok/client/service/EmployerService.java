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
import at.undok.undok.client.repository.PersonRepo;
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
    private final PersonRepo personRepo;

    public EmployerDto setEmployer(EmployerForm employerForm) {
        Person employerPerson = new Person();

        Address address = new Address();
        if (employerForm.getEmployerCity() != null) {
            address.setCity(employerForm.getEmployerCity());
        }
        if (employerForm.getEmployerZipCode() != null) {
            address.setZipCode(employerForm.getEmployerZipCode());
        }
        if (employerForm.getEmployerStreet() != null) {
            address.setStreet(employerForm.getEmployerStreet());
        }
        if (employerForm.getEmployerCountry() != null) {
            address.setCountry(employerForm.getEmployerCountry());
        }
        Address savedAddress = addressRepo.save(address);

        if (employerForm.getEmployerFirstName() != null) {
            employerPerson.setFirstName(employerForm.getEmployerFirstName());
        }
        if (employerForm.getEmployerLastName() != null) {
            employerPerson.setLastName(employerForm.getEmployerLastName());
        }
        if (employerForm.getEmployerEmail() != null) {
            employerPerson.setEmail(employerForm.getEmployerEmail());
        }
        if (employerForm.getEmployerTelephone() != null) {
            employerPerson.setTelephone(employerForm.getEmployerTelephone());
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

        Employer toBeUpdatedEmployer = employerRepo.findById(employerDto.getId()).orElseThrow();
        Person toBeUpdatedPerson = personRepo.findById(employerDto.getPerson().getId()).orElseThrow();
        Address toBeUpdatedAddress = addressRepo.findById(employerDto.getPerson().getAddress().getId()).orElseThrow();

        toBeUpdatedAddress.setStreet(employerDto.getPerson().getAddress().getStreet());
        toBeUpdatedAddress.setCity(employerDto.getPerson().getAddress().getCity());
        toBeUpdatedAddress.setCountry(employerDto.getPerson().getAddress().getCountry());
        toBeUpdatedAddress.setZipCode(employerDto.getPerson().getAddress().getZipCode());
        Address savedAddress = addressRepo.save(toBeUpdatedAddress);

        toBeUpdatedPerson.setAddress(savedAddress);
        toBeUpdatedPerson.setFirstName(employerDto.getPerson().getFirstName());
        toBeUpdatedPerson.setLastName(employerDto.getPerson().getLastName());
        toBeUpdatedPerson.setEmail(employerDto.getPerson().getEmail());
        toBeUpdatedPerson.setTelephone(employerDto.getPerson().getTelephone());
        toBeUpdatedPerson.setUpdatedAt(LocalDateTime.now());
        Person savedPerson = personRepo.save(toBeUpdatedPerson);

        toBeUpdatedEmployer.setPerson(savedPerson);
        toBeUpdatedEmployer.setPosition(employerDto.getPosition());
        toBeUpdatedEmployer.setCompany(employerDto.getCompany());
        toBeUpdatedEmployer.setPosition(employerDto.getPosition());
        toBeUpdatedEmployer.setUpdatedAt(LocalDateTime.now());
        Employer savedEmployer = employerRepo.save(toBeUpdatedEmployer);

        return modelMapper.map(savedEmployer, EmployerDto.class);
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
