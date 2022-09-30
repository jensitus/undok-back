package at.undok.undok.client.controller;

import at.undok.common.message.Message;
import at.undok.undok.client.api.EmployerApi;
import at.undok.undok.client.model.dto.ClientEmployerJobDescriptionDto;
import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.form.EmployerForm;
import at.undok.undok.client.service.EmployerService;
import liquibase.pro.packaged.M;
import liquibase.pro.packaged.R;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;
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
    public List<EmployerDto> getAll(UUID clientId) {
        return employerService.getEmployers(clientId);
    }

    @Override
    public List<ClientEmployerJobDescriptionDto> getByClientId(UUID clientId) {
        return employerService.getByClientId(clientId);
    }

    @Override
    public Long getNumberOfEmployers() {
        return employerService.getNumberOfEmployers();
    }

    @Override
    public EmployerDto updateEmployer(UUID id, EmployerDto employerDto) {
        return employerService.updateEmployer(employerDto);
    }

    @Override
    public ResponseEntity setStatusDeleted(UUID id) {
        employerService.setStatusDeleted(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Message> handle(NoSuchElementException e) {
        return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Message> handle(RuntimeException e) {
        return new ResponseEntity<>(new Message(e.getMessage()), HttpStatus.CONFLICT);
    }
}
