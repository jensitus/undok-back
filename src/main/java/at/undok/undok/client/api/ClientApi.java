package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.CounselingForm;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/service/undok/clients")
@PreAuthorize("hasRole('USER')")
public interface ClientApi {

    @CrossOrigin
    @PostMapping("/create")
    ResponseEntity<ClientDto> createClient(@RequestBody ClientForm clientForm);

    @GetMapping("/all/{page}/{size}")
    Map<String, Map> getAllClients(@PathVariable("page") int page, @PathVariable("size") int size);

    @GetMapping("/{id}")
    ClientDto getClientById(@PathVariable("id") UUID id);

    @PostMapping("/{id}/counseling")
    CounselingDto createCounseling(@PathVariable("id") UUID clientId, @RequestBody CounselingForm counselingForm);

    @GetMapping("/count")
    Long getNumberOfClients();

    @PutMapping("/{id}/update")
    ClientDto updateClient(@PathVariable("id") UUID clientId, @RequestBody ClientDto clientDto);

    @GetMapping("/all")
    List<AllClientDto> getAll();

    @DeleteMapping("/{id}/set-deleted")
    ResponseEntity setStatusDeleted(@PathVariable("id") UUID clientPersonId);

    @DeleteMapping("/{id}")
    ResponseEntity deleteClient(@PathVariable("id") UUID clientId);

    @GetMapping("/csv")
    ResponseEntity getClientsCsv();

}
