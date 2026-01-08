package at.undok.undok.client.api;

import at.undok.undok.client.model.form.ClientEmployerForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/service/undok/client/employers")
@PreAuthorize("hasRole('USER')")
public interface ClientEmployerApi {

    @GetMapping("/{employer_id}/client/{client_id}/present")
    boolean checkEmployerClient(@PathVariable("employer_id") UUID employerId,
                                @PathVariable("client_id") UUID clientId);

    @PostMapping("/{employer_id}/client/{client_id}/create")
    boolean addEmployerToClient(@PathVariable("employer_id") UUID employerId,
                                @PathVariable("client_id") UUID clientId,
                                @RequestBody ClientEmployerForm clientEmployerForm);

    @DeleteMapping("/{client_employer_id}/client/{client_id}/delete")
    boolean removeEmployerFromClient(@PathVariable("client_employer_id") UUID clientEmployerId,
                                     @PathVariable("client_id") UUID clientId);

    @PutMapping("/{employer_id}/client/{client_id}")
    boolean updateClientEmployerJobDescription(@PathVariable("employer_id") UUID employerId,
                                               @PathVariable("client_id") UUID clientId,
                                               @RequestBody ClientEmployerForm clientEmployerForm);

}
