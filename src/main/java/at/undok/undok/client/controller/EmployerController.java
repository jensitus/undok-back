package at.undok.undok.client.controller;

import at.undok.undok.client.api.EmployerApi;
import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.form.EmployerForm;
import at.undok.undok.client.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EmployerController implements EmployerApi {

    private final EmployerService employerService;

    @Override
    public EmployerDto createEmployer(EmployerForm employerForm) {
        return employerService.setEmployer(employerForm);
    }

    @Override
    public EmployerDto getEmployerById(UUID id) {
        return employerService.getEmployerById(id);
    }

    @Override
    public List<EmployerDto> getAll() {
        return employerService.getEmployers();
    }

    @Override
    public List<EmployerDto> getByClientId(UUID clientId) {
        return employerService.getByClientId(clientId);
    }

    @Override
    public Long getNumberOfEmployers() {
        return employerService.getNumberOfEmployers();
    }
}
