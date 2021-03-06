package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Counseling;
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
    List<CounselingDto> getAllCounselings();

    @PutMapping("/{id}/update")
    CounselingDto updateCounseling(@PathVariable("id") UUID counselingId, @RequestBody CounselingDto counselingDto);

    @PutMapping("/{id}/set-or-update-comment/")
    CounselingDto setCommentOnCounseling(@PathVariable("id") UUID counselingId, @RequestBody String comment);

    @DeleteMapping("/{id}")
    void deleteCounseling(@PathVariable("id") UUID counselingId);


}
