package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Counseling;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080", "https://undok.herokuapp.com"}, maxAge = 3600)
@RequestMapping("/service/undok/counselings")
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

}
