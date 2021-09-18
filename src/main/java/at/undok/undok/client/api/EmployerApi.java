package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.form.EmployerForm;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080", "https://undok.herokuapp.com"}, maxAge = 3600)
@RequestMapping("/service/undok/employers")
public interface EmployerApi {

    @PostMapping("/create")
    EmployerDto createEmployer(@RequestBody EmployerForm employerForm);

    @GetMapping("/{id}")
    EmployerDto getEmployerById(@PathVariable("id") UUID id);

    @GetMapping("/all/client/{client_id}")
    List<EmployerDto> getAll(@PathVariable("client_id") UUID clientId);

    @GetMapping("/{client_id}/by-client")
    List<EmployerDto> getByClientId(@PathVariable("client_id") UUID clientId);

    @GetMapping("/count")
    Long getNumberOfEmployers();

}
