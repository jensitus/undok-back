package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.ClientCount;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.dto.PersonDto;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.CounselingForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RequestMapping("/service/undok/clients")
public interface ClientApi {

    @CrossOrigin
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    ClientDto createClient(@RequestBody ClientForm clientForm);

    @GetMapping("/all/{page}/{size}")
    Map<String, Map> getAllClients(@PathVariable("page") int page, @PathVariable("size") int size);

    @GetMapping("/{id}")
    PersonDto getClientById(@PathVariable("id") UUID id);

    @PostMapping("/{id}/counseling")
    CounselingDto createCounseling(@PathVariable("id") UUID clientId, @RequestBody CounselingForm counselingForm);


}
