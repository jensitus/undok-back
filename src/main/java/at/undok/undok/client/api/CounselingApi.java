package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.AllCounselingDto;
import at.undok.undok.client.model.dto.CounselingDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/service/undok/counselings")
@PreAuthorize("hasRole('USER')")
public interface CounselingApi {

    @GetMapping("/count")
    Long getNumberOfCounselings();

    @GetMapping("/past/")
    List<CounselingDto> getOldCounselings();

    @GetMapping("/all")
    ResponseEntity<List<AllCounselingDto>> getAllCounselings();

    @PutMapping("/{id}/update")
    CounselingDto updateCounseling(@PathVariable("id") UUID counselingId, @RequestBody CounselingDto counselingDto);

    @PutMapping("/{id}/set-or-update-comment/")
    CounselingDto setCommentOnCounseling(@PathVariable("id") UUID counselingId, @RequestBody String comment);

    @DeleteMapping("/{id}")
    void deleteCounseling(@PathVariable("id") UUID counselingId);

    @GetMapping("/{id}")
    CounselingDto getSingleCounseling(@PathVariable("id") UUID counselingId);

    @GetMapping("/csv")
    ResponseEntity<Resource> getFile();

    @PutMapping("/{id}/set-required-time")
    ResponseEntity<CounselingDto> setRequiredTime(@PathVariable("id") UUID counselingId, @RequestBody Integer requiredTime);

    @GetMapping("by-client/{client-id}/order/{order}")
    ResponseEntity<List<CounselingDto>> getCounselingByClientId(@PathVariable("client-id") UUID clientId, @PathVariable("order") String order);


}
