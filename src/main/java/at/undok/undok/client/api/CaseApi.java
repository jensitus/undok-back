package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.form.CloseCaseForm;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/service/undok/case")
@PreAuthorize("hasRole('USER')")
public interface CaseApi {

    @GetMapping("{id}")
    ResponseEntity<CaseDto> getCase(@PathVariable("id") UUID id);

    @PostMapping
    ResponseEntity<CaseDto> createCase(@RequestBody CaseDto caseDto);

    @PutMapping("{id}/close")
    ResponseEntity<CaseDto> closeCase(@PathVariable("id") UUID id, @RequestBody CloseCaseForm form);

    @PutMapping("{id}/reopen")
    ResponseEntity<CaseDto> reopenCase(@PathVariable("id") UUID id);


}
