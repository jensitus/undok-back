package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Counseling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RequestMapping("/service/undok/counselings")
public interface CounselingApi {

    @GetMapping("/count")
    Long getNumberOfCounselings();

    @GetMapping("/past/")
    List<CounselingDto> getOldCounselings();

    @GetMapping("/all")
    List<CounselingDto> getAllCounselings();

}
