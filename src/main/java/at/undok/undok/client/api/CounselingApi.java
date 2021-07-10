package at.undok.undok.client.api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RequestMapping("/service/undok/counselings")
public interface CounselingApi {

    @GetMapping("/count")
    Long getNumberOfCounselings();

}
