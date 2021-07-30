package at.undok.undok.client.service;

import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.entity.Employer;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.EmployerForm;
import at.undok.undok.client.repository.EmployerRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepo employerRepo;
    private final ModelMapper modelMapper;

    public EmployerDto setEmployer(EmployerForm employerForm) {
        Person employerPerson = new Person();
        employerPerson.setFirstName(employerForm.getEmployerFirstName());
        employerPerson.setLastName(employerForm.getEmployerLastName());
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

    public List<EmployerDto> getEmployers() {
        List<Employer> employers = employerRepo.findAll();
        List<EmployerDto> dtoList = modelMapper.map(employers, List.class);
        return dtoList;
    }
}
