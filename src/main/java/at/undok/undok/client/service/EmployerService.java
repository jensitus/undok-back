package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.entity.ClientEmployer;
import at.undok.undok.client.model.entity.Employer;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.form.EmployerForm;
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
    private final ModelMapper modelMapper;
    private final EntityToDtoMapper entityToDtoMapper;
    private final AttributeEncryptor attributeEncryptor;
    private final ClientEmployerService clientEmployerService;

    public EmployerDto setEmployer(EmployerForm employerForm) {
        Person employerPerson = new Person();
        employerPerson.setFirstName(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerFirstName()));
        employerPerson.setLastName(attributeEncryptor.convertToDatabaseColumn(employerForm.getEmployerLastName()));
        employerPerson.setCreatedAt(LocalDateTime.now());

        Employer employer = new Employer();
        employer.setCompany(employerForm.getEmployerCompany());
        employer.setPosition(employerForm.getEmployerPosition());
        employer.setPerson(employerPerson);

        Employer e = employerRepo.save(employer);

        return modelMapper.map(e, EmployerDto.class);
    }

    public EmployerDto getEmployerById(UUID id) {
        Employer employer = employerRepo.getOne(id);
        return modelMapper.map(employer, EmployerDto.class);
    }

    public List<EmployerDto> getEmployers(UUID clientId) {
        List<Employer> employers = employerRepo.findAll();
        List<EmployerDto> employerDtos = entityToDtoMapper.convertEmployerListToDto(employers);
        for (EmployerDto e : employerDtos) {
            if (clientEmployerService.checkClientEmployer(e.getId(), clientId) == true) {
                employerDtos.remove(e);
            }
        }
        return employerDtos;
    }

    public List<EmployerDto> getByClientId(UUID clientId) {
        List<ClientEmployer> clientEmployers = clientEmployerService.getByClientId(clientId);
        List<EmployerDto> employerDtos = new ArrayList<>();
        for (ClientEmployer ce : clientEmployers) {
            Employer employer = employerRepo.getOne(ce.getEmployerId());
            EmployerDto employerDto = entityToDtoMapper.mapEmployerToDto(employer);
            employerDtos.add(employerDto);
        }
        return employerDtos;
    }

    public Long getNumberOfEmployers() {
        return employerRepo.count();
    }


}
