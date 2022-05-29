package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.ClientEmployerJobDescriptionDto;
import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.entity.Address;
import at.undok.undok.client.model.entity.ClientEmployer;
import at.undok.undok.client.model.entity.Employer;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.form.EmployerForm;
import at.undok.undok.client.repository.AddressRepo;
import at.undok.undok.client.repository.EmployerRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        employer.setCreatedAt(LocalDateTime.now());
        employer.setPerson(employerPerson);
        employer.getPerson().setAddress(savedAddress);
        Employer eWithAddress = employerRepo.save(employer);
        return modelMapper.map(eWithAddress, EmployerDto.class);
    }

    public EmployerDto getEmployerById(UUID id) {
        Employer employer = employerRepo.getOne(id);
        return modelMapper.map(employer, EmployerDto.class);
    }

    public List<EmployerDto> getEmployers(UUID clientId) {
        List<Employer> employers = employerRepo.findAllByOrderByCreatedAtDesc();
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
        return employerRepo.count();
    }

}
