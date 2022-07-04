package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.ClientEmployerJobDescriptionDto;
import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.form.EmployerForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/service/undok/employers")
@PreAuthorize("hasRole('USER')")
public interface EmployerApi {

    @PostMapping("/create")
    EmployerDto createEmployer(@RequestBody EmployerForm employerForm);

    @GetMapping("/{id}")
    EmployerDto getEmployerById(@PathVariable("id") UUID id);

    @GetMapping("/all/")
    List<EmployerDto> getAll(@RequestParam(value = "client_id", required = false) UUID clientId);

    @GetMapping("/{client_id}/by-client")
    List<ClientEmployerJobDescriptionDto> getByClientId(@PathVariable("client_id") UUID clientId);

    @GetMapping("/count")
    Long getNumberOfEmployers();

    @PutMapping("/{id}")
    EmployerDto updateEmployer(@PathVariable("id") UUID id, @RequestBody EmployerDto employerDto);

}
