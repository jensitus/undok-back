package at.undok.undok.client.api;

import at.undok.undok.client.model.form.ClientEmployerForm;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080", "https://undok.herokuapp.com"}, maxAge = 3600)
@RequestMapping("/service/undok/client/employers")
public interface ClientEmployerApi {

    @GetMapping("/{employer_id}/client/{client_id}/present")
    boolean checkEmployerClient(@PathVariable("employer_id") UUID employerId, @PathVariable("client_id") UUID clientId);

    @PostMapping("/{employer_id}/client/{client_id}/create")
    boolean addEmployerToClient(@PathVariable("employer_id") UUID employerId,
                                @PathVariable("client_id") UUID clientId,
                                @RequestBody ClientEmployerForm clientEmployerForm);

    @DeleteMapping("/{employer_id}/client/{client_id}/delete")
    boolean removeEmployerFromClient(@PathVariable("employer_id") UUID employerId, @PathVariable("client_id") UUID clientId);

}
