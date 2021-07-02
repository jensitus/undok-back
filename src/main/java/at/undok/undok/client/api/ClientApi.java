package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.PersonDto;
import at.undok.undok.client.model.form.ClientForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/undok/clients")
public interface ClientApi {

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    ClientDto createClient(@RequestBody ClientForm clientForm);

    @GetMapping("/all/{page}/{size}")
    List<PersonDto> getAllClients(@PathVariable("page") int page, @PathVariable("size") int size);

    @GetMapping("/{id}")
    PersonDto getClientById(@PathVariable("id") UUID id);

}
